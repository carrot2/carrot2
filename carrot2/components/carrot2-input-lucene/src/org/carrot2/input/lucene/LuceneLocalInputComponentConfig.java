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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Searcher;

/**
 * Configuration for the Lucene input component. Contains everything that the
 * {@link LuceneLocalInputComponentFactoryConfig} has, plus the {@link Searcher} and the
 * {@link Analyzer} the input component is to use.
 * 
 * @author Stanislaw Osinski
 */
public final class LuceneLocalInputComponentConfig
{
    /** Factory config */
    final LuceneLocalInputComponentFactoryConfig factoryConfig;

    /** Lucene Searcher */
    final Searcher searcher;

    /** Lucene IndexReader */
    final IndexReader indexReader;

    /** Lucene Analyzer */
    final Analyzer analyzer;

    public LuceneLocalInputComponentConfig(
        LuceneLocalInputComponentFactoryConfig factoryConfig, IndexReader indexReader,
        Searcher searcher, Analyzer analyzer)
    {
        super();
        this.factoryConfig = factoryConfig;
        this.indexReader = indexReader;
        this.searcher = searcher;
        this.analyzer = analyzer;
    }
}
