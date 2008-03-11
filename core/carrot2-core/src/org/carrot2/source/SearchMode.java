package org.carrot2.source;

/**
 * Search mode for data source components that implement
 * parallel request to some search service.
 */
public enum SearchMode
{
    /**
     * In this mode, an initial search request is performed to estimate
     * the number of documents available on the server. Then the requested
     * number of documents is adjusted according to the number of documents
     * available to minimize the number of requests.
     */
    CONSERVATIVE,

    /**
     * In this mode, the number of requested documents is divided by the
     * maximum number of documents the search engine can return in a single
     * request. The result is the number of <b>concurrent</b> requests
     * launched to the search service.
     * <p>
     * Note that speculative threads cause larger load on the search service
     * and will exhaust your request pool quicker (if it is limited).
     */
    SPECULATIVE,
}
