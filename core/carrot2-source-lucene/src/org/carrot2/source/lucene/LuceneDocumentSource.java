
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.lucene;

import java.io.IOException;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.*;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.slf4j.Logger;

import com.google.common.collect.Maps;

/**
 * A {@link IDocumentSource} fetching {@link Document}s from a local Apache Lucene index.
 * The index should be binary-compatible with the Lucene version actually imported by this
 * plugin.
 */
@Bindable(prefix = "LuceneDocumentSource", inherit = AttributeNames.class)
public final class LuceneDocumentSource extends ProcessingComponentBase implements
    IDocumentSource
{
    /** Logger for this class. */
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(LuceneDocumentSource.class);

    /*
     * Register selected SimpleXML wrappers for Lucene data types.
     */
    static
    {
        SimpleXmlWrappers.addWrapper(FSDirectory.class, FSDirectoryWrapper.class, false);
        SimpleXmlWrappers.addWrapper(StandardAnalyzer.class, StandardAnalyzerWrapper.class, true);
    }

    @Processing
    @Input
    @Attribute(key = AttributeNames.RESULTS, inherit = true)
    @IntRange(min = 1)
    public int results = 100;

    @Processing
    @Output
    @Attribute(key = AttributeNames.RESULTS_TOTAL, inherit = true)
    public long resultsTotal;

    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    @Internal
    public Collection<Document> documents;

    /**
     * Search index {@link Directory}. Must be unlocked for reading.
     * 
     * @label Index directory
     * @group Index properties
     * @level Basic
     */
    @Input
    @Attribute
    @Init
    @Processing
    @Required
    @Internal(configuration = true)
    @ImplementingClasses(classes =
    {
        RAMDirectory.class, FSDirectory.class
    }, strict = false)
    public Directory directory;

    /**
     * {@link Analyzer} used at indexing time. The same analyzer should be used for
     * querying.
     * 
     * @label Analyzer
     * @group Index properties
     * @level Medium
     */
    @Input
    @Init
    @Processing
    @Required
    @Attribute
    @ImplementingClasses(classes =
    {
        SimpleAnalyzer.class, StandardAnalyzer.class, WhitespaceAnalyzer.class
    }, strict = false)
    public Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

    /**
     * {@link IFieldMapper} provides the link between Carrot2 {@link Document} fields and
     * Lucene index fields.
     * 
     * @label Field mapper
     * @group Index field mapping
     * @level Advanced
     */
    @Input
    @Init
    @Processing
    @Required
    @Attribute
    @Internal
    @ImplementingClasses(classes =
    {
        SimpleFieldMapper.class
    }, strict = false)
    public IFieldMapper fieldMapper = new SimpleFieldMapper();

    /**
     * A pre-parsed {@link Query} object or a {@link String} parsed using the built-in
     * {@link QueryParser} over a set of search fields returned from the
     * {@link #fieldMapper}.
     *
     * @label Query
     * @group Search query
     * @level Basic
     */
    @Input
    @Processing
    @Attribute(key = AttributeNames.QUERY, inherit = false) /* false intentional! */
    @Required
    @ImplementingClasses(classes =
    {
        Query.class, String.class
    }, strict = false)
    @NotBlank
    public Object query;

    /**
     * A context-shared map between {@link Directory} objects and any opened
     * {@link IndexSearcher}s.
     */
    private IdentityHashMap<Directory, IndexSearcher> openIndexes;

    /**
     * Controller context serving as the synchronization monitor when opening indices.
     */
    private IControllerContext context;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(IControllerContext context)
    {
        super.init(context);
        this.context = context;

        synchronized (context)
        {
            final String key = AttributeUtils.getKey(getClass(), "openIndexes");
            if (context.getAttribute(key) == null)
            {
                context.setAttribute(key, Maps.newIdentityHashMap());
                context.addListener(new IControllerContextListener()
                {
                    public void beforeDisposal(IControllerContext context)
                    {
                        closeAllIndexes();
                    }
                });
            }

            this.openIndexes = (IdentityHashMap<Directory, IndexSearcher>) context
                .getAttribute(key);
        }
    }

    /*
     * 
     */
    public void process() throws ProcessingException
    {
        try
        {
            final SearchEngineResponse response = fetchSearchResponse();
            documents = response.results;
            resultsTotal = response.getResultsTotal();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(ProcessingException.class, e);
        }
    }

    /**
     * Fetch search engine response.
     */
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        if (directory == null)
        {
            throw new ProcessingException("Directory attribute must not be empty.");
        }

        if (this.query instanceof String)
        {
            final String [] searchFields = fieldMapper.getSearchFields();
            if (searchFields == null || searchFields.length == 0)
            {
                throw new ProcessingException(
                    "At least one search field must be given for a plain text query. "
                        + "Alternatively, use a Lucene Query object.");
            }

            final String textQuery = (String) query;
            if (StringUtils.isEmpty(textQuery))
            {
                throw new ProcessingException(
                    "An instantiated Lucene Query object or a non-empty "
                        + "plain text query is required.");
            }

            if (searchFields.length == 1)
            {
                query = new QueryParser(
                    Version.LUCENE_30, searchFields[0], analyzer).parse(textQuery);
            }
            else
            {
                query = new MultiFieldQueryParser(
                    Version.LUCENE_30, searchFields, analyzer)
                    .parse(textQuery);
            }
        }

        final SearchEngineResponse response = new SearchEngineResponse();
        final IndexSearcher searcher = indexOpen(directory);
        final TopDocs docs = searcher.search((Query) query, null, results);

        response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, docs.totalHits);

        for (ScoreDoc scoreDoc : docs.scoreDocs)
        {
            final Document doc = new Document();
            final org.apache.lucene.document.Document luceneDoc = searcher
                .doc(scoreDoc.doc);

            this.fieldMapper.map((Query) query, analyzer, luceneDoc, doc);
            response.results.add(doc);
        }

        return response;
    }

    /**
     * Close all opened indexes in the shared context.
     */
    private void closeAllIndexes()
    {
        synchronized (context)
        {
            for (IndexSearcher searcher : openIndexes.values())
            {
                try
                {
                    searcher.close();
                }
                catch (IOException e)
                {
                    logger.warn("Could not close search index: " + searcher, e);
                }
            }
        }
    }

    /**
     * Open or retrieve an open handle to an {@link IndexSearcher}.
     */
    private IndexSearcher indexOpen(Directory directory) throws ProcessingException
    {
        synchronized (context)
        {
            IndexSearcher searcher = openIndexes.get(directory);
            if (searcher == null)
            {
                try
                {
                    searcher = new IndexSearcher(directory, true);
                    openIndexes.put(directory, searcher);
                }
                catch (IOException e)
                {
                    throw ExceptionUtils.wrapAs(ProcessingException.class, e);
                }
            }
            return searcher;
        }
    }
}
