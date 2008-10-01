package org.carrot2.source.lucene;

import java.io.IOException;
import java.util.IdentityHashMap;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

import com.google.common.collect.Maps;

/**
 * A {@link DocumentSource} fetching {@link Document}s from a local Apache Lucene index.
 * The index should be binary-compatible with the Lucene version actually imported by this
 * plugin.
 */
@Bindable(prefix = "LuceneDocumentSource")
public final class LuceneDocumentSource extends SimpleSearchEngine
{
    /** Logger for this class. */
    private final static Logger logger = Logger.getLogger(LuceneDocumentSource.class);

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
    public Analyzer analyzer = new StandardAnalyzer();

    /**
     * {@link FieldMapper} provides the link between Carrot2 {@link Document} fields and
     * Lucene index fields.
     * 
     * @label Field mapper
     * @group Index field mapping
     * @level Advanced
     */
    @Input
    @Init
    @Required
    @Attribute
    @Internal
    @ImplementingClasses(classes =
    {
        SimpleFieldMapper.class
    }, strict = false)
    public FieldMapper fieldMapper = new SimpleFieldMapper();

    /**
     * A pre-parsed {@link Query} object or <code>null</code> if default
     * <code>query</code> attribute should be parsed using a built-in
     * {@link QueryParser} over a set of search fields returned from the
     * {@link #fieldMapper}.
     * 
     * @label Lucene query
     * @group Search query
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    @Internal    
    @ImplementingClasses(classes =
    {
        Query.class
    }, strict = false)
    public Query luceneQuery;

    /**
     * A context-shared map between {@link Directory} objects and any opened
     * {@link IndexSearcher}s.
     */
    private IdentityHashMap<Directory, IndexSearcher> openIndexes;

    /**
     * Controller context serving as the synchronization monitor when opening indices.
     */
    private ControllerContext context;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void init(ControllerContext context)
    {
        super.init(context);
        this.context = context;

        synchronized (context)
        {
            final String key = AttributeUtils.getKey(getClass(), "openIndexes");
            if (context.getAttribute(key) == null)
            {
                context.setAttribute(key, Maps.newIdentityHashMap());
                context.addListener(new ControllerContextListener()
                {
                    public void beforeDisposal(ControllerContext context)
                    {
                        closeAllIndexes();
                    }
                });
            }

            this.openIndexes = (IdentityHashMap<Directory, IndexSearcher>) context
                .getAttribute(key);
        }
    }

    /**
     * Fetch search engine response.
     */
    @Override
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        if (directory == null)
        {
            throw new ProcessingException("Directory attribute must not be empty.");
        }

        if (this.luceneQuery == null)
        {
            final String [] searchFields = fieldMapper.getSearchFields();
            if (searchFields == null || searchFields.length == 0)
            {
                throw new ProcessingException(
                    "A Lucene query or at least one search field must be specified.");
            }

            if (searchFields.length == 1)
            {
                luceneQuery = new QueryParser(searchFields[0], analyzer)
                    .parse(super.query);
            }
            else
            {
                luceneQuery = new MultiFieldQueryParser(searchFields, analyzer)
                    .parse(super.query);
            }
        }

        /*
         * TODO: We currently ignore the start parameter. This could be implemented
         * efficiently with a custom HitCollector (so that irrelevant documents are not
         * fetched).
         */

        final SearchEngineResponse response = new SearchEngineResponse();
        final IndexSearcher searcher = indexOpen(directory);
        final TopDocs docs = searcher.search(luceneQuery, null, results);

        response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, docs.totalHits);

        for (ScoreDoc scoreDoc : docs.scoreDocs)
        {
            final Document doc = new Document();
            final org.apache.lucene.document.Document luceneDoc = searcher
                .doc(scoreDoc.doc);

            this.fieldMapper.map(luceneQuery, analyzer, luceneDoc, doc);
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
                    searcher = new IndexSearcher(directory);
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
