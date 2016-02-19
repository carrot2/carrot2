
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

package org.carrot2.source;

/**
 * Metadata describing {@link MultipageSearchEngine} characteristics.
 */
public final class MultipageSearchEngineMetadata
{
    /**
     * Maximum number of results returned per page.
     */
    public final int resultsPerPage;

    /**
     * Maximum reachable result index.
     */
    public final int maxResultIndex;

    /**
     * If <code>false</code>, the start position of the search is determined by the
     * result index, which is the case for most search engines. If <code>true</code>,
     * the start position is determined by the page index.
     */
    public final boolean incrementByPage;

    /**
     * Creates search engine metadata with {@link #incrementByPage} set to
     * <code>false</code>.
     */
    public MultipageSearchEngineMetadata(int resultsPerPage, int maxResultIndex)
    {
        this(resultsPerPage, maxResultIndex, false);
    }

    public MultipageSearchEngineMetadata(int resultsPerPage, int maxResultIndex,
        boolean incrementByPage)
    {
        this.incrementByPage = incrementByPage;
        this.maxResultIndex = maxResultIndex;
        this.resultsPerPage = resultsPerPage;
    }
}
