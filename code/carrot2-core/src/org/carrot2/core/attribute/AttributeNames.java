package org.carrot2.core.attribute;

/**
 * Certain constant attribute names. Note that not all attributes need to be specified
 * here.
 */
public final class AttributeNames
{
    /**
     * Index (zero-based) of the first document/ search result to fetch.
     * 
     * @label Start Index
     */
    public static final String START = "start";

    /**
     * Number of documents/ search results to fetch.
     * 
     * @label Results
     */
    public static final String RESULTS = "results";

    /**
     * Query to be executed by the search engine/ document retrieval system.
     * 
     * @label Query
     */
    public static final String QUERY = "query";

    /**
     * Estimated total number of matching documents.
     * 
     * @label Total Results
     */
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * Documents returned by the search engine/ document retrieval system.
     * 
     * @label Documents
     */
    public static final String DOCUMENTS = "documents";

    /**
     * Clusters created by the clustering algorithm.
     * 
     * @label Clusters
     */
    public static final String CLUSTERS = "clusters";

    /*
     * 
     */
    private AttributeNames()
    {
        // No instances.
    }
}
