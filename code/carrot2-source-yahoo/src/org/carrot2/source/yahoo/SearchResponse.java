package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.carrot2.core.Document;

/**
 * 
 */
final class SearchResponse
{
    public final long totalResults;
    public final int firstResultIndex;
    public final int resultsReturned;

    /** All parsed results. */
    public final ArrayList<Document> results = new ArrayList<Document>(100);

    /*
     * 
     */
    public SearchResponse(long totalResults, int firstResultPosition, int resultsReturned)
    {
        this.totalResults = totalResults;
        this.firstResultIndex = firstResultPosition;
        this.resultsReturned = resultsReturned;
    }
}
