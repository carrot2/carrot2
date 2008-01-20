
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

package org.carrot2.core.fetcher;

import org.carrot2.core.clustering.RawDocument;

/**
 * A single results returned from the {@link SingleFetcher}.
 * 
 * @author Dawid Weiss
 */
public final class SearchResult
{
    public final int at;
    public final long totalEstimated;
    public final Throwable error;
    public final RawDocument [] results;

    public SearchResult(RawDocument [] results, int at, long totalEstimated)
    {
        this.results = results;
        this.totalEstimated = totalEstimated;
        this.at = at;
        error = null;
    }

    public SearchResult(Throwable t)
    {
        this.error = t;
        this.results = null;
        this.totalEstimated = -1;
        this.at = -1;
    }
    
    public String toString() {
        if (this.error == null) {
            return "Search result ["
                + "at=" + at
                + ", count=" + results.length
                + ", total=" + totalEstimated
                + "]";
        } else {
            return "Search result [error=" + error.toString() + "]";
        }
    }
}