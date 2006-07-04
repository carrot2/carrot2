/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.carrot.input.lucene;

import java.io.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;

import com.dawidweiss.carrot.core.local.*;

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
     * Default search fields: {@link #DEFAULT_TITLE_FIELD},
     * {@link #DEFAULT_SUMMARY_FIELD}
     */
    public static final String [] DEFAULT_SEARCH_FIELDS = new String []
    {
        DEFAULT_TITLE_FIELD, DEFAULT_SUMMARY_FIELD
    };

    /**
     * Lucene searcher. Searchers are thread-safe, so we can have a shared
     * instance for all component that we produce
     */
    private Searcher searcher;
    private IndexReader indexReader;
    private String indexDirectory;

    /** Lucene analyzer factory. */
    private AnalyzerFactory analyzerFactory;

    /** Field names */
    private String titleField;
    private String summaryField;
    private String urlField;

    /** Search fields */
    private String [] searchFields;

    /**
     * Default Analyzer Factory returns {@link StandardAnalyzer}wrapped with
     * {@link PorterStemFilter}
     */
    public static final AnalyzerFactory DEFAULT_ANALYZER_FACTORY = PorterAnalyzerFactory.INSTANCE;

    public LuceneLocalInputComponentFactory(String indexDirectory)
        throws IOException
    {
        this((IndexReader) null);
        this.indexDirectory = indexDirectory;
    }

    public LuceneLocalInputComponentFactory(IndexReader indexReader)
        throws IOException
    {
        this(indexReader, DEFAULT_ANALYZER_FACTORY);
    }

    /**
     * Creates a factory that will produce components using
     * {@link IndexSearcher}to perform searches, given analyzer and default
     * field names.
     * 
     * @throws IOException
     */
    public LuceneLocalInputComponentFactory(String indexDirectory,
        AnalyzerFactory analyzerFactory) throws IOException
    {
        this((IndexReader) null, analyzerFactory);
        this.indexDirectory = indexDirectory;
    }

    /**
     * Creates a factory that will produce components using
     * {@link IndexSearcher}to perform searches, given analyzer and default
     * field names.
     */
    public LuceneLocalInputComponentFactory(IndexReader indexReader,
        AnalyzerFactory analyzerFactory)
    {
        this(null, analyzerFactory, DEFAULT_SEARCH_FIELDS, DEFAULT_TITLE_FIELD,
            DEFAULT_SUMMARY_FIELD, DEFAULT_URL_FIELD);
        this.indexReader = indexReader;
    }

    /**
     * Creates a factory that will produce components using
     * {@link IndexSearcher}to perform searches, given analyzer and default
     * field names.
     */
    public LuceneLocalInputComponentFactory(Searcher searcher,
        AnalyzerFactory analyzerFactory, String [] searchFields,
        String titleField, String summaryField, String urlField)
    {
        this.searcher = searcher;
        this.analyzerFactory = analyzerFactory;
        this.titleField = titleField;
        this.summaryField = summaryField;
        this.urlField = urlField;
        this.searchFields = searchFields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponentFactory#getInstance()
     */
    public LocalComponent getInstance()
    {
        initSearcher();
        return new LuceneLocalInputComponent(searcher, analyzerFactory
            .getInstance(), searchFields, titleField, summaryField, urlField);
    }

    /**
     * Lazily initialize the searcher.
     */
    private synchronized void initSearcher()
    {
        if (searcher == null)
        {
            if (indexReader == null)
            {
                try
                {
                    indexReader = IndexReader.open(indexDirectory);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("Cannot initialize IndexReader");
                }
            }
            searcher = new IndexSearcher(indexReader);
        }
    }
}