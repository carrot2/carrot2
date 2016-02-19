
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.attribute;

import java.util.List;

import org.carrot2.core.*;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Output;

/**
 * Attributes shared and inherited by many clustering algorithms. Extracted for consistency.
 */
@Bindable
public final class CommonAttributes
{
    // No instances.
    private CommonAttributes()
    {
        // no instances.
    }

    /**
     * Index of the first document/ search result to fetch. The index starts at zero.
     */
    @Input
    @Attribute(key = "start")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.QUERY)
    @Label("Start index")
    public int start;

    /**
     * Maximum number of documents/ search results to fetch. The query hint can be used
     * by clustering algorithms to avoid creating trivial clusters (combination of query words).
     */
    @Input
    @Attribute(key = "results")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.QUERY)
    @Label("Results")
    public int results;

    /**
     * Query to perform.
     */
    @Input
    @Attribute(key = "query")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.QUERY)
    @Label("Query")
    public String query;

    /**
     * Estimated total number of matching documents.
     */
    @Output
    @Attribute(key = "results-total")
    @Label("Total results")
    @Group(DefaultGroups.RESULT_INFO)
    public Long resultsTotal;

    /**
     * Documents returned by the search engine/ document retrieval system or
     * documents passed as input to the clustering algorithm.
     */
    @Input
    @Output
    @Attribute(key = "documents")
    @Label("Documents")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.DOCUMENTS)
    public List<Document> documents;

    /**
     * Clusters created by the clustering algorithm.
     */
    @Output
    @Attribute(key = "clusters")
    @Label("Clusters")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)
    public List<Cluster> clusters;

    /**
     * Total processing time in milliseconds. A sum of processing times of all components in the chain.
     * Total processing time may be greater than the sum of
     * {@link #processingTimeTotal} and {@link #processingTimeAlgorithm}.
     */
    @Output
    @Attribute(key = "processing-time-total")
    @Label("Total processing time")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)    
    public Long processingTimeTotal;

    /**
     * Data source processing time in milliseconds. A sum of processing times of all
     * {@link org.carrot2.core.IDocumentSource}s in the chain, including the
     * {@link org.carrot2.core.IProcessingComponent#beforeProcessing()} and
     * {@link org.carrot2.core.IProcessingComponent#afterProcessing()} hooks.
     */
    @Output
    @Attribute(key = "processing-time-source")
    @Label("Data source processing time")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)
    public Long processingTimeSource;

    /**
     * Algorithm processing time in milliseconds. A sum of processing times of all
     * {@link org.carrot2.core.IClusteringAlgorithm}s in the chain, including the
     * {@link org.carrot2.core.IProcessingComponent#beforeProcessing()} and
     * {@link org.carrot2.core.IProcessingComponent#afterProcessing()} hooks.
     */
    @Output
    @Attribute(key = "processing-time-algorithm")
    @Label("Clustering algorithm processing time")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)
    public Long processingTimeAlgorithm;

    /**
     * Processing result title. A typical title for a processing result will be the query
     * used to fetch documents from that source. For certain document sources the query
     * may not be needed (on-disk XML, feed of syndicated news); in such cases, the input
     * component should set its title properly for visual interfaces such as the
     * workbench.
     */
    @Output
    @Attribute(key = "processing-result.title")
    @Label("Title")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.RESULT_INFO)
    public String processingResultTitle;
}
