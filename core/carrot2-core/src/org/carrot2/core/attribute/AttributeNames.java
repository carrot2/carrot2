
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;

/**
 * Certain constant attribute names. Note that not all attributes need to be specified
 * here.
 * 
 * 
 */
@Bindable(prefix = "")
public final class AttributeNames
{
    /**
     * Index of the first document/ search result to fetch. The index starts at zero.
     * 
     * @label Start Index
     * @level Advanced
     * @group Search query
     */
    @Attribute(key = "start")
    public static final String START = "start";

    /**
     * Maximum number of documents/ search results to fetch. The query hint can be used
     * by clustering algorithms to avoid creating trivial clusters (combination of query words).
     * 
     * @label Results
     * @level Basic
     * @group Search query
     */
    @Attribute(key = "results")
    public static final String RESULTS = "results";

    /**
     * Query to perform.
     * 
     * @label Query
     * @level Basic
     * @group Search query
     */
    @Attribute(key = "query")
    public static final String QUERY = "query";

    /**
     * Estimated total number of matching documents.
     * 
     * @label Total Results
     * @group Search request information
     */
    @Attribute(key = "results-total")
    public static final String RESULTS_TOTAL = "results-total";

    /**
     * Documents returned by the search engine/ document retrieval system.
     * 
     * @label Documents
     * @level Basic
     * @group Documents
     */
    @Attribute(key = "documents")
    public static final String DOCUMENTS = "documents";

    /**
     * Clusters created by the clustering algorithm.
     * 
     * @label Clusters
     * @group Clusters
     */
    @Attribute(key = "clusters")
    public static final String CLUSTERS = "clusters";

    /**
     * Total processing time in milliseconds. A sum of processing times of all components in the chain.
     * Total processing time may be greater than the sum of
     * {@link #PROCESSING_TIME_SOURCE} and {@link #PROCESSING_TIME_ALGORITHM}.
     * 
     * @label Total Processing Time
     * @group Processing status
     */
    @Attribute(key = "processing-time-total")
    public static final String PROCESSING_TIME_TOTAL = "processing-time-total";

    /**
     * Data source processing time in milliseconds. A sum of processing times of all
     * {@link IDocumentSource}s in the chain, including the
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Data Source Processing Time
     * @group Data source status
     */
    @Attribute(key = "processing-time-source")
    public static final String PROCESSING_TIME_SOURCE = "processing-time-source";

    /**
     * Algorithm processing time in milliseconds. A sum of processing times of all
     * {@link IClusteringAlgorithm}s in the chain, including the
     * {@link IProcessingComponent#beforeProcessing()} and
     * {@link IProcessingComponent#afterProcessing()} hooks.
     * 
     * @label Clustering Algorithm Processing Time
     * @group Clustering algorithm status
     */
    @Attribute(key = "processing-time-algorithm")
    public static final String PROCESSING_TIME_ALGORITHM = "processing-time-algorithm";

    /**
     * Processing result title. A typical title for a processing result will be the query
     * used to fetch documents from that source. For certain document sources the query
     * may not be needed (on-disk XML, feed of syndicated news); in such cases, the input
     * component should set its title properly for visual interfaces such as the
     * workbench.
     * 
     * @label Title
     * @level Advanced
     * @group Search request information
     */
    @Attribute(key = "processing-result.title")
    public static final String PROCESSING_RESULT_TITLE = "processing-result.title";

    /*
     *
     */
    private AttributeNames()
    {
        // No instances.
    }
}
