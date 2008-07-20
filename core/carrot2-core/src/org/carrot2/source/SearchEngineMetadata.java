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

    /**
     * If <code>false</code>, the start position of the search is determined by the result
     * index, which is the case for most search engines. If <code>true</code>, the start
     * position is determined by the page index.
     */
    public final boolean incrementByPage;

    /**
     * Creates search engine metadata with {@link #incrementByPage} set to
     * <code>false</code>.
     */
    public SearchEngineMetadata(int resultsPerPage, int maxResultIndex)
    {
        this(resultsPerPage, maxResultIndex, false);
    }

    public SearchEngineMetadata(int resultsPerPage, int maxResultIndex,
        boolean incrementByPage)
    {
        this.incrementByPage = incrementByPage;
        this.maxResultIndex = maxResultIndex;
        this.resultsPerPage = resultsPerPage;
    }
}
