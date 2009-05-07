
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.output.metrics;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Sets;

/**
 * Computes precision, recall and F-metric for individual clusters and an average of these
 * across top-level clusters.
 * <p>
 * Metrics will be calculated only if all input documents have non-blank
 * {@link Document#TOPIC}s.
 * </p>
 */
@Bindable
public class PrecisionRecallMetric extends IdealPartitioningBasedClusteringMetric
{
    /**
     * Key for the precision value for a cluster.
     */
    public static final String PRECISION = "precision";

    /**
     * Key for the recall value for a cluster.
     */
    public static final String RECALL = "recall";

    /**
     * Key for the F-measure value for a cluster.
     */
    public static final String F_MEASURE = "f-measure";

    /**
     * Average precision of the whole cluster set.
     */
    @Processing
    @Output
    @Attribute(key = "average-precision")
    public double averagePrecision;

    /**
     * Average recall of the whole cluster set.
     */
    @Processing
    @Output
    @Attribute(key = "average-recall")
    public double averageRecall;

    /**
     * Average F-measure of the whole cluster set.
     */
    @Processing
    @Output
    @Attribute(key = "average-f-measure")
    public double averageFMeasure;

    /**
     * Calculate F-measure.
     */
    @Processing
    @Input
    @Attribute
    public boolean enabled = true;

    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Processing
    @Input
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters;

    public void calculate()
    {
        final int topicCount = getTopicCount(documents);
        if (topicCount == 0)
        {
            return;
        }

        if (clusters.size() == 0)
        {
            return;
        }

        final Map<String, Set<Document>> documentsByTopic = getDocumentsByTopic(documents);

        double recallSum = 0;
        double precisionSum = 0;
        double fMeasureSum = 0;
        int clusterCount = 0;

        for (Cluster cluster : clusters)
        {
            final List<Document> clusterDocuments = cluster.getAllDocuments();
            if (cluster.getAttribute(Cluster.OTHER_TOPICS) != null
                || clusterDocuments.size() == 0)
            {
                continue;
            }

            final String majorTopic = getMajorTopic(cluster.getAllDocuments());
            final Set<Document> commonDocuments = Sets.newHashSet(documentsByTopic
                .get(majorTopic));
            final int topicDocumentsCount = commonDocuments.size();
            commonDocuments.retainAll(clusterDocuments);

            final double precision = commonDocuments.size()
                / (double) clusterDocuments.size();
            final double recall = commonDocuments.size() / (double) topicDocumentsCount;
            final double fMeasure = 2 * precision * recall / (precision + recall);

            cluster.setAttribute(IdealPartitioningBasedClusteringMetric.MAJOR_TOPIC,
                majorTopic);
            cluster.setAttribute(PRECISION, precision);
            cluster.setAttribute(RECALL, recall);
            cluster.setAttribute(F_MEASURE, fMeasure);

            recallSum += recall;
            precisionSum += precision;
            fMeasureSum += fMeasure;
            clusterCount++;
        }

        averageFMeasure = fMeasureSum / clusterCount;
        averagePrecision = precisionSum / clusterCount;
        averageRecall = recallSum / clusterCount;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
