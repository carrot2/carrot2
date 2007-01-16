package org.carrot2.core.fetcher;

import org.carrot2.core.ProcessingException;

/**
 * A fetcher downloads search results for a given query.
 * 
 * @author Dawid Weiss
 */
public abstract class SingleFetcher
{
    /**
     * 
     */
    public abstract SearchResult fetch(String query, int startAt) throws ProcessingException;
}
