package org.carrot2.core.attribute;

import org.carrot2.core.*;

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

    /**
     * Total processing time. A sum of processing times of all components in the chain.
     * Total processing time may be greater than the sum of
     * {@link #PROCESSING_TIME_SOURCE} and {@link #PROCESSING_TIME_ALGORITHM}.
     * 
     * @label Total Processing Time
     */
    public static final String PROCESSING_TIME_TOTAL = "processing-time-total";

    /**
     * Data source processing time. A sum of processing times of all
     * {@link DocumentSource}s in the chain, including the
     * {@link ProcessingComponent#beforeProcessing()} and
     * {@link ProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Data Source Processing Time
     */
    public static final String PROCESSING_TIME_SOURCE = "processing-time-source";

    /**
     * Algorithm processing time. A sum of processing times of all
     * {@link ClusteringAlgorithm}s in the chain, including the
     * {@link ProcessingComponent#beforeProcessing()} and
     * {@link ProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Clustering Algorithm Processing Time
     */
    public static final String PROCESSING_TIME_ALGORITHM = "processing-time-algorithm";

    /*
     *
     */
    private AttributeNames()
    {
        // No instances.
    }
}
