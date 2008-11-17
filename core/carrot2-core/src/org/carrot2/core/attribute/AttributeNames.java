
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

package org.carrot2.core.attribute;

import javax.security.auth.callback.LanguageCallback;

import org.carrot2.core.*;

/**
 * Certain constant attribute names. Note that not all attributes need to be specified
 * here.
 */
public final class AttributeNames
{
    /**
     * Index of the first document/ search result to fetch. The index starts at zero.
     * 
     * @label Start Index
     * @level Medium
     * @group Search query
     */
    public static final String START = "start";

    /**
     * Maximum number of documents/ search results to fetch.
     * 
     * @label Results
     * @level Basic
     * @group Search query
     */
    public static final String RESULTS = "results";

    /**
     * Query to perform.
     * 
     * @label Query
     * @level Basic
     * @group Search query
     */
    public static final String QUERY = "query";

    /**
     * Estimated total number of matching documents.
     * 
     * @label Total Results
     * @group Search request statistics
     */
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * Documents returned by the search engine/ document retrieval system.
     * 
     * @label Documents
     * @group Documents
     */
    public static final String DOCUMENTS = "documents";

    /**
     * Clusters created by the clustering algorithm.
     * 
     * @label Clusters
     * @group Clusters
     */
    public static final String CLUSTERS = "clusters";

    /**
     * Total processing time. A sum of processing times of all components in the chain.
     * Total processing time may be greater than the sum of
     * {@link #PROCESSING_TIME_SOURCE} and {@link #PROCESSING_TIME_ALGORITHM}.
     * 
     * @label Total Processing Time
     * @group Processing status
     */
    public static final String PROCESSING_TIME_TOTAL = "processing-time-total";

    /**
     * Data source processing time. A sum of processing times of all
     * {@link IDocumentSource}s in the chain, including the
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Data Source Processing Time
     * @group Data source status
     */
    public static final String PROCESSING_TIME_SOURCE = "processing-time-source";

    /**
     * Algorithm processing time. A sum of processing times of all
     * {@link IClusteringAlgorithm}s in the chain, including the
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Clustering Algorithm Processing Time
     * @group Clustering algorithm status
     */
    public static final String PROCESSING_TIME_ALGORITHM = "processing-time-algorithm";

    /**
     * Processing language hint. Suggestion for the clustering algorithm to perform
     * clustering in the specified language. Based on the suggestion, the algorithm can
     * determine e.g. the stemmer or list of stop words the algorithm uses. A mismatch
     * between the processing language hint and the actual language of processed documents
     * may result in poor clustering quality.
     * 
     * @label Processing Language
     * @level Basic
     * @group Preprocessing
     * @see org.carrot2.text.linguistic.LanguageCode
     */
    public static final String ACTIVE_LANGUAGE = "active-language";

    /*
     *
     */
    private AttributeNames()
    {
        // No instances.
    }
}
