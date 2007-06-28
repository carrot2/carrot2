
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

/**
 * @author Stanislaw Osinski
 */
public class LuceneLocalInputComponentConfig
{
    /** Factory config */
    final LuceneLocalInputComponentFactoryConfig factoryConfig;

    /** Lucene Searcher */
    final Searcher searcher;

    /** Lucene Analyzer */
    final Analyzer analyzer;

    /**
     * @param factoryConfig
     * @param searcher
     * @param analyzer
     */
    public LuceneLocalInputComponentConfig(
        LuceneLocalInputComponentFactoryConfig factoryConfig, Searcher searcher,
        Analyzer analyzer)
    {
        super();
        this.factoryConfig = factoryConfig;
        this.searcher = searcher;
        this.analyzer = analyzer;
    }
}
