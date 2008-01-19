
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

import org.carrot2.core.ProcessingException;

/**
 * A fetcher instance downloads search results for a given query.
 * 
 * @author Dawid Weiss
 */
public abstract class SingleFetcher
{
    /**
     * @param query The query to be sent to the search engine.
     * @param startAt Starting index from which results should be retrieved (zero-based).
     * @param fetchSize The number of results to retrieve. This should not exceed
     * a single page window as given in the constructor of {@link ParallelFetcher}.
     */
    public abstract SearchResult fetch(String query, int startAt, int fetchSize)
        throws ProcessingException; 
}
