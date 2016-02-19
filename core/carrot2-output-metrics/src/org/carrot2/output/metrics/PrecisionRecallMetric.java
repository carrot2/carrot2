
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

package org.carrot2.output.metrics;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.MathUtils;
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.collect.*;

/**
 * Computes precision, recall and F-metric for all partitions against the provided
 * clusters.
 * <p>
 * Metrics will be calculated only if all input documents have non-blank
 * {@link Document#PARTITIONS}.
 * </p>
 */
@Bindable
public class PrecisionRecallMetric extends IdealPartitioningBasedMetric
{
    /**
     * Partition on which the cluster achieved best F-Score value. Value type:
     * <code>Object</code>. See {@link Document#PARTITIONS} for more information.
     */
    public final static String BEST_F_MEASURE_PARTITION = "best-f-measure-partition";

    /**
     * Average precision of the whole cluster set, weighted by cluster size.
     */
    @Processing
    @Output
    @Attribute
    public Double weightedAveragePrecision;

    /**
     * Average recall of the whole cluster set, weighted by cluster size.
     */
    @Processing
    @Output
    @Attribute
    public Double weightedAverageRecall;

    /**
     * Average F-measure of the whole cluster set, weighted by cluster size.
     */
    @Processing
    @Output
    @Attribute
    public Double weightedAverageFMeasure;

    /**
     * Precision by partition.
     */
    @Processing
    @Output
    @Attribute
    public Map<Object, Double> precisionByPartition;

    /**
     * Recall by partition.
     */
    @Processing
    @Output
    @Attribute
    public Map<Object, Double> recallByPartition;

    /**
     * F-measure by partition.
     */
    @Processing
    @Output
    @Attribute
    public Map<Object, Double> fMeasureByPartition;

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
        final int partitionCount = getPartitionsCount(documents);
        if (partitionCount == 0)
        {
            return;
        }

        if (clusters.size() == 0)
        {
            return;
        }

        final SetMultimap<Object, Document> documentsByPartition = getDocumentsByPartition(documents);
        final Set<Object> partitions = getPartitions(documents);

        precisionByPartition = Maps.newHashMap();
        recallByPartition = Maps.newHashMap();
        fMeasureByPartition = Maps.newHashMap();

        double recallSum = 0;
        double precisionSum = 0;
        double fMeasureSum = 0;
        int partitionDocumentsCountSum = 0;

        for (Object partition : partitions)
        {
            final Set<Document> partitionDocuments = documentsByPartition.get(partition);
            final int partitionDocumentsCount = partitionDocuments.size();
            double partitionFMeasure = 0;
            double partitionPrecision = 0;
            double partitionRecall = 0;
            Cluster bestFMeasureCluster = null;

            for (Cluster cluster : clusters)
            {
                final List<Document> clusterDocuments = cluster.getAllDocuments();
                if (cluster.isOtherTopics() || clusterDocuments.size() == 0)
                {
                    continue;
                }

                final Set<Document> commonDocuments = Sets.newHashSet(partitionDocuments);
                commonDocuments.retainAll(clusterDocuments);

                final double precision = commonDocuments.size()
                    / (double) clusterDocuments.size();
                final double recall = commonDocuments.size()
                    / (double) partitionDocumentsCount;
                final double fMeasure = MathUtils.harmonicMean(precision, recall);

                if (fMeasure > partitionFMeasure)
                {
                    partitionFMeasure = fMeasure;
                    partitionPrecision = precision;
                    partitionRecall = recall;
                    bestFMeasureCluster = cluster;
                }
            }

            recallSum += partitionRecall * partitionDocumentsCount;
            precisionSum += partitionPrecision * partitionDocumentsCount;
            fMeasureSum += partitionFMeasure * partitionDocumentsCount;
            partitionDocumentsCountSum += partitionDocumentsCount;

            recallByPartition.put(partition, partitionRecall);
            precisionByPartition.put(partition, partitionPrecision);
            fMeasureByPartition.put(partition, partitionFMeasure);
            if (bestFMeasureCluster != null)
            {
                bestFMeasureCluster.setAttribute(BEST_F_MEASURE_PARTITION, partition);
            }
        }

        // Dividing by partitionDocumentsCountSum rather than by the number of documents
        // because partitionDocumentsCountSum can be larger than the number of documents
        // if the partitions have overlapping documents.
        weightedAveragePrecision = precisionSum / partitionDocumentsCountSum;
        weightedAverageRecall = recallSum / partitionDocumentsCountSum;
        weightedAverageFMeasure = fMeasureSum / partitionDocumentsCountSum;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
