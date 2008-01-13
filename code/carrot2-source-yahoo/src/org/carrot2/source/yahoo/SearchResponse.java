package org.carrot2.source.yahoo;

import java.util.ArrayList;

import org.carrot2.core.Document;

/**
 * Parsed search response from Yahoo!
 */
final class SearchResponse
{
    public final long resultsTotal;
    public final int firstResultIndex;
    public final int resultsReturned;

    /** All parsed results. */
    public final ArrayList<Document> results = new ArrayList<Document>(100);

    /*
     * 
     */
    public SearchResponse(long totalResults, int firstResultPosition, int resultsReturned)
    {
        this.resultsTotal = totalResults;
        this.firstResultIndex = firstResultPosition;
        this.resultsReturned = resultsReturned;
    }
}
