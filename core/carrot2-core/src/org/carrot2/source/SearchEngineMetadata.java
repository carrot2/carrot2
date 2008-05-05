package org.carrot2.source;

/**
 * Metadata describing {@link SearchEngine} characteristics.
 */
public final class SearchEngineMetadata
{
    /**
     * Maximum number of results returned per page.
     */
    public final int resultsPerPage;

    /**
     * Maximum reachable result index.
     */
    public final int maxResultIndex;
 
    
    public SearchEngineMetadata(int resultsPerPage, int maxResultIndex)
    {
        this.resultsPerPage = resultsPerPage;
        this.maxResultIndex = maxResultIndex;
    }
}
