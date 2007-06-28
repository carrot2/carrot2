
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

/**
 * All settings required to perform a search in Lucene: location of the index,
 * searched fields, analyzer etc.
 *
 * @author Dawid Weiss
 * @author Sairaj Sunil
 * @author Stanislaw Osinski
 */
public final class LuceneLocalInputComponentFactoryConfig
{
    /** Lucene fields to be searched */
    final String [] searchFields;

    /** Content fields */
    final String titleField;
    final String summaryField;
    final String urlField;

    /** Config of the summarization to be applied to the summary field */
    final LuceneSummarizerConfig summarizerConfig;

    /**
     * Creates a default Lucene search config with summarization switched off.
     *
     * @param searcher
     * @param analyzer
     * @param searchFields
     * @param titleField
     * @param summaryField
     * @param urlField
     */
    public LuceneLocalInputComponentFactoryConfig(
        String [] searchFields, String titleField, String summaryField,
        String urlField)
    {
        this(searchFields, titleField, summaryField,
            urlField, NO_SUMMARIES);
    }

    public LuceneLocalInputComponentFactoryConfig(
        String [] searchFields, String titleField, String summaryField,
        String urlField, LuceneSummarizerConfig summarizerConfig)
    {
        this.searchFields = searchFields;
        this.titleField = titleField;
        this.summaryField = summaryField;
        this.urlField = urlField;
        this.summarizerConfig = summarizerConfig;
    }

    /**
     * Setting of the Lucene's snippet generator to be used on the summary
     * field. Please see the constants for example summarizer configurations.
     *
     * @author Stanislaw Osinski
     */
    public static class LuceneSummarizerConfig
    {
        /** Maximum number of fragments to extract */
        final int maxFragments;

        /** Formatter to be used */
        final Formatter formatter;

        public LuceneSummarizerConfig(final int maxFragments,
            final Formatter formatter)
        {
            super();
            this.maxFragments = maxFragments;
            this.formatter = formatter;
        }
    }

    /**
     * A Lucene summarizer config that completely switches off the snippet
     * generation. With this config, the full content of the summary field will
     * be passed for clustering.
     */
    public static final LuceneSummarizerConfig NO_SUMMARIES = new LuceneSummarizerConfig(
        0, null);

    /**
     * A Lucene summarizer config that enables short (one fragment) summaries
     * with HTML tags enclosing query term occurrences.
     */
    public static final LuceneSummarizerConfig SHORT_HTML_SUMMARY = new LuceneSummarizerConfig(
        1, new SimpleHTMLFormatter());

    /**
     * A Lucene summarizer config that enables longer (up to 4 fragments) plain
     * text summaries (no highlighting of query terms).
     */
    public static final LuceneSummarizerConfig LONG_PLAIN_TEXT_SUMMARY = new LuceneSummarizerConfig(
        4, new PlainTextFormatter());
}
