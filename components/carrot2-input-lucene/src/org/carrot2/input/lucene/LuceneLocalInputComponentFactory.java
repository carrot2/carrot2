
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LuceneLocalInputComponentFactory implements LocalComponentFactory
{
    /** Default title field name */
    public static final String DEFAULT_TITLE_FIELD = "title";

    /** Default title field name */
    public static final String DEFAULT_SUMMARY_FIELD = "summary";

    /** Default title field name */
    public static final String DEFAULT_URL_FIELD = "url";

    /**
     * Default search fields: {@link #DEFAULT_TITLE_FIELD}, {@link #DEFAULT_SUMMARY_FIELD}
     */
    public static final String [] DEFAULT_SEARCH_FIELDS = new String []
    {
        DEFAULT_TITLE_FIELD, DEFAULT_SUMMARY_FIELD
    };

    /**
     * All information required to perform a search in Lucene. Searchers are thread-safe, so we can have a shared
     * instance for all component that we produce.
     */
    private final LuceneLocalInputComponentFactoryConfig luceneFactoryConfig;
    private Searcher searcher;
    private IndexReader indexReader;
    private String indexDirectory;

    /** Lucene analyzer factory. */
    private AnalyzerFactory analyzerFactory;

    /**
     * Default Analyzer Factory returns {@link StandardAnalyzer}wrapped with {@link PorterStemFilter}
     */
    public static final AnalyzerFactory DEFAULT_ANALYZER_FACTORY = PorterAnalyzerFactory.INSTANCE;

    /**
     * CCreates a Lucene input component factory that produces {@link LuceneLocalInputComponent} instances that read
     * index at the specified location with default field names and the default analyzer.
     *
     * @param indexDirectory
     * @throws IOException
     */
    public LuceneLocalInputComponentFactory(String indexDirectory) throws IOException
    {
        this((Searcher) null);
        this.indexDirectory = indexDirectory;
    }

    /**
     * Creates a Lucene input component factory that produces {@link LuceneLocalInputComponent} instances that use the
     * specified {@link Searcher} and default field names and the default analyzer.
     *
     * @param searcher
     */
    public LuceneLocalInputComponentFactory(Searcher searcher)
    {
        this(searcher, new LuceneLocalInputComponentFactoryConfig(DEFAULT_SEARCH_FIELDS, DEFAULT_TITLE_FIELD,
            DEFAULT_SUMMARY_FIELD, DEFAULT_URL_FIELD), DEFAULT_ANALYZER_FACTORY);
    }

    /**
     * Creates a Lucene input component factory that produces {@link LuceneLocalInputComponent} instances that read
     * index at the specified location and uses the specified search configuration (field names) and the specified
     * analyzer.
     *
     * @throws IOException
     */
    public LuceneLocalInputComponentFactory(String indexDirectory, LuceneLocalInputComponentFactoryConfig luceneSearchConfig,
        AnalyzerFactory analyzerFactory) throws IOException
    {
        this((Searcher) null, luceneSearchConfig, analyzerFactory);
        this.indexDirectory = indexDirectory;
    }

    /**
     * Creates a Lucene input component factory that produces {@link LuceneLocalInputComponent} instances using the
     * specified searcher, the specified search configuration (field names) and the specified analyzer.
     */
    public LuceneLocalInputComponentFactory(Searcher searcher, LuceneLocalInputComponentFactoryConfig luceneSearchConfig,
        AnalyzerFactory analyzerFactory)
    {
        this.searcher = searcher;
        this.luceneFactoryConfig = luceneSearchConfig;
        this.analyzerFactory = analyzerFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.carrot2.core.LocalComponentFactory#getInstance()
     */
    public LocalComponent getInstance()
    {
        getCreateIndexReaderAndSearcher();
        return new LuceneLocalInputComponent(new LuceneLocalInputComponentConfig(
            luceneFactoryConfig, indexReader, searcher, analyzerFactory.getInstance()));
    }

    /**
     * Lazily initialize the searcher.
     */
    private synchronized void getCreateIndexReaderAndSearcher()
    {
        if (searcher == null)
        {
            if (indexDirectory == null)
            {
                throw new RuntimeException("Searcher not available (index directory null, no explicit searcher given).");
            }
            try
            {
                indexReader = IndexReader.open(indexDirectory);
                searcher = new IndexSearcher(indexReader);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Cannot initialize IndexReader");
            }
        }
    }
}