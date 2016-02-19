
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.carrot2.core.Document;
import org.carrot2.core.Document.IDocumentSerializationListener;
import org.carrot2.core.IControllerContext;
import org.carrot2.core.IControllerContextListener;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.NotBlank;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A {@link IDocumentSource} fetching {@link Document}s from a local Apache Lucene index.
 * The index should be binary-compatible with the Lucene version actually imported by this
 * plugin.
 */
@Bindable(prefix = "LuceneDocumentSource", inherit = CommonAttributes.class)
public final class LuceneDocumentSource extends ProcessingComponentBase implements
    IDocumentSource
{
    protected final static String INDEX_PROPERTIES = "Index properties";

    /** Logger for this class. */
    private final static Logger logger = org.slf4j.LoggerFactory
        .getLogger(LuceneDocumentSource.class);

    /*
     * Register selected SimpleXML wrappers for Lucene data types.
     */
    static
    {
        SimpleXmlWrappers.addWrapper(
            FSDirectory.class, 
            FSDirectoryWrapper.class, 
            false);
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
     * Search index {@link org.apache.lucene.store.Directory}. Must be unlocked for
     * reading.
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
    @Label("Index directory")
    @Level(AttributeLevel.BASIC)
    @Group(INDEX_PROPERTIES)    
    public Directory directory;

    /**
     * {@link org.apache.lucene.analysis.Analyzer} used at indexing time. The same
     * analyzer should be used for querying.
     */
    @Input
    @Init
    @Processing
    @Required
    @Attribute
    @Internal(configuration = false)
    @ImplementingClasses(classes =
        { /* No suggestions for default implementations. */ }, strict = false)
    @Label("Analyzer")
    @Level(AttributeLevel.MEDIUM)
    @Group(INDEX_PROPERTIES)    
    public Analyzer analyzer = new StandardAnalyzer();

    /**
     * {@link IFieldMapper} provides the link between Carrot2
     * {@link org.carrot2.core.Document} fields and Lucene index fields.
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
    @Label("Field mapper")
    @Level(AttributeLevel.ADVANCED)
    @Group(SimpleFieldMapper.INDEX_FIELD_MAPPING)
    public IFieldMapper fieldMapper = new SimpleFieldMapper();

    /**
     * A pre-parsed {@link org.apache.lucene.search.Query} object or a {@link String}
     * parsed using the built-in classic QueryParser over a
     * set of search fields returned from the {@link #fieldMapper}.
     */
    @Input
    @Processing
    @Attribute(key = AttributeNames.QUERY, inherit = false) // false intentional!
    @Required
    @ImplementingClasses(classes =
    {
        Query.class, String.class
    }, strict = false)
    @NotBlank
    @Label("Query")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.QUERY)    
    public Object query;

    /**
     * Keeps references to Lucene document instances in Carrot2 documents. Please bear in
     * mind two limitations:
     * <ul>
     * <li><strong>Lucene documents will not be serialized to XML/JSON.</strong>
     * Therefore, they can only be accessed when invoking clustering through Carrot2 Java
     * API. To pass some of the fields of Lucene documents to Carrot2 XML/JSON output,
     * implement a custom {@link IFieldMapper} that will store those fields as regular
     * Carrot2 fields.</li>
     * <li><strong>Increased memory usage</strong> when using a {@link org.carrot2.core.Controller}
     * {@link org.carrot2.core.ControllerFactory#createCachingPooling(Class...) configured to cache} the
     * output from {@link LuceneDocumentSource}.</li>
     * </ul>
     */
    @Input
    @Processing
    @Attribute
    @Internal
    @Label("Keep Lucene documents")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.RESULT_INFO)
    public boolean keepLuceneDocuments = false;

    /**
     * Carrot2 {@link Document} field that stores the original Lucene document instance.
     * Keeping of Lucene document instances is disabled by default. Enable it using the
     * {@link #keepLuceneDocuments} attribute.
     */
    public final static String LUCENE_DOCUMENT_FIELD = "luceneDocument";

    /**
     * A context-shared map between {@link org.apache.lucene.store.Directory} objects and
     * any opened {@link org.apache.lucene.search.IndexSearcher}s.
     */
    private IdentityHashMap<Directory, IndexSearcher> openIndexes;

    /**
     * Controller context serving as the synchronization monitor when opening indices.
     */
    private IControllerContext context;

    /**
     * A serialization listener that prevents Lucene documents from appearing in the
     * Carrot2 documents serialized to XML/JSON.
     */
    private static final IDocumentSerializationListener removeLuceneDocument = new IDocumentSerializationListener()
    {
        @Override
        public void beforeSerialization(Document document,
            Map<String, ?> otherFieldsForSerialization)
        {
            otherFieldsForSerialization.remove(LUCENE_DOCUMENT_FIELD);
        }
    };

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
                query = new QueryParser(searchFields[0], analyzer)
                    .parse(textQuery);
            }
            else
            {
                query = new MultiFieldQueryParser(searchFields, analyzer).parse(textQuery);
            }
        }

        final SearchEngineResponse response = new SearchEngineResponse();
        final IndexSearcher searcher = indexOpen(directory);
        final TopDocs docs = searcher.search((Query) query, results);

        response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, docs.totalHits);

        for (ScoreDoc scoreDoc : docs.scoreDocs)
        {
            final Document doc = new Document();
            final org.apache.lucene.document.Document luceneDoc = searcher
                .doc(scoreDoc.doc);

            // Set score before mapping to give the mapper a chance to override it
            doc.setScore((double) scoreDoc.score);

            if (keepLuceneDocuments)
            {
                doc.setField(LUCENE_DOCUMENT_FIELD, luceneDoc);
                doc.addSerializationListener(removeLuceneDocument);
            }

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
                    searcher.getIndexReader().close();
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
                    searcher = new IndexSearcher(DirectoryReader.open(directory));
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
