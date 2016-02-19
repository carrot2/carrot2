
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
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * Computes Normalized Mutual Information (NMI) metric for the cluster set.
 * <p>
 * Metrics will be calculated only if all input documents have non-blank
 * {@link Document#PARTITIONS}.
 * </p>
 */
@Bindable
public class NormalizedMutualInformationMetric extends IdealPartitioningBasedMetric
{
    /**
     * Normalized Mutual Information of the whole cluster set.
     */
    @Processing
    @Output
    @Attribute
    public Double normalizedMutualInformation;

    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    public List<Document> documents;

    @Processing
    @Input
    @Attribute(key = AttributeNames.CLUSTERS)
    public List<Cluster> clusters;

    /**
     * Calculate Normalized Mutual Information metric.
     */
    @Processing
    @Input
    @Attribute
    public boolean enabled = true;

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

        final Set<Object> partitions = getPartitions(documents);
        final SetMultimap<Object, Document> documentsByPartition = getDocumentsByPartition(documents);
        final Map<Object, Integer> documentCountByPartition = getDocumentCountByPartition(documents);
        final int documentCount = documents.size();

        if (partitions.size() <= 1)
        {
            normalizedMutualInformation = 0.0;
            return;
        }

        final Collection<Integer> partitionSizes = Maps.transformValues(
            documentsByPartition.asMap(), new Function<Collection<Document>, Integer>()
            {
                public Integer apply(Collection<Document> documents)
                {
                    return documents.size();
                }
            }).values();
        double partitionEntropy = entropy(documentCount, partitionSizes
            .toArray(new Integer [partitionSizes.size()]));

        final List<Integer> clusterSizes = Lists.transform(clusters,
            new Function<Cluster, Integer>()
            {
                public Integer apply(Cluster cluster)
                {
                    return cluster.size();
                }
            });
        double clusterEntropy = entropy(documentCount, clusterSizes
            .toArray(new Integer [clusterSizes.size()]));

        double mutualInformation = 0;
        for (Cluster cluster : this.clusters)
        {
            final int clusterSize = cluster.size();
            for (Object partition : partitions)
            {
                final List<Document> clusterDocuments = cluster.getAllDocuments();
                if (cluster.isOtherTopics() || clusterDocuments.size() == 0)
                {
                    continue;
                }

                final Set<Document> commonDocuments = Sets
                    .newHashSet(documentsByPartition.get(partition));
                commonDocuments.retainAll(clusterDocuments);
                int commonDocumentsCount = commonDocuments.size();

                if (commonDocumentsCount != 0)
                {
                    mutualInformation += (commonDocumentsCount / (double) documentCount)
                        * Math.log(documentCount
                            * commonDocumentsCount
                            / (double) (clusterSize * documentCountByPartition
                                .get(partition)));
                }
            }
        }

        normalizedMutualInformation = mutualInformation
            / ((clusterEntropy + partitionEntropy) / 2);
    }

    private double entropy(int count, Integer... elements)
    {
        double entropy = 0;
        for (int d : elements)
        {
            if (d != 0)
            {
                entropy += d / (double) count * Math.log(d / (double) count);
            }
        }
        return -entropy;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
