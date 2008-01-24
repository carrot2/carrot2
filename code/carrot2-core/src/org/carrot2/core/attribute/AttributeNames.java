package org.carrot2.core.attribute;

/**
 * Certain constant attribute names. Note that not all attributes need to be specified
 * here.
 */
public final class AttributeNames
{
    /**
     * Start fetching documents (search results) from this value (zero-based).
     */
    public static final String START = "start";
    
    /**
     * Fetch this number of documents (search results) from
     * the source.
     */
    public static final String RESULTS = "results";
    
    /**
     * Pass this query to the search engine/ document retrieval
     * system.
     */
    public static final String QUERY = "query";

    /**
     * The number of matching documents (estimated total).
     */
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * A collection of returned documents.
     */
    public static final String DOCUMENTS = "documents";

    /**
     * A collection of created clusters.
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
