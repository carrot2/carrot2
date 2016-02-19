
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

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Computes cluster contamination. If a cluster groups documents found in the same
 * {@link Document#PARTITIONS}, its contamination is 0. If a cluster groups an equally
 * distributed mix of all partitions, its contamination is 1.0. For a full definition,
 * please see section 4.4.1 of <a
 * href="http://project.carrot2.org/publications/osinski04-dimensionality.pdf">this
 * work</a>.
 * <p>
 * Contamination is calculated for top-level clusters only, taking into account documents
 * from the cluster and all subclusters. Finally, contamination will be calculated only if
 * all input documents have non-blank {@link Document#PARTITIONS}s.
 * </p>
 */
@Bindable
public class ContaminationMetric extends IdealPartitioningBasedMetric
{
    /**
     * Key for the contamination value of a cluster.
     */
    public static final String CONTAMINATION = "contamination";

    /**
     * Average contamination of the whole cluster set, weighted by the size of cluster.
     */
    @Processing
    @Output
    @Attribute
    public double weightedAverageContamination;

    /**
     * Calculate contamination metric.
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

        int weightSum = 0;
        double contaminationSum = 0;

        for (Cluster cluster : clusters)
        {
            if (cluster.isOtherTopics())
            {
                continue;
            }
            final double contamination = calculate(cluster, partitionCount);
            cluster.setAttribute(CONTAMINATION, contamination);

            contaminationSum += contamination * cluster.size();
            weightSum += cluster.size();
        }

        weightedAverageContamination = contaminationSum / weightSum;
    }

    @SuppressWarnings("unchecked")
    double calculate(Cluster cluster, int partitionCount)
    {
        int clusterPartitionAssignments = 0;
        for (Document document : cluster.getAllDocuments())
        {
            clusterPartitionAssignments += ((Collection<Object>) document
                .getField(Document.PARTITIONS)).size();
        }

        final double worstCaseH = calculateWorstCaseH(clusterPartitionAssignments, partitionCount);
        if (worstCaseH == 0)
        {
            return 0;
        }
        else
        {
            return calculateH(cluster) / worstCaseH;
        }
    }

    int calculateH(Cluster cluster)
    {
        final Map<Object, Integer> documentCountByPartition = getDocumentCountByPartition(cluster
            .getAllDocuments());

        final ArrayList<Integer> counts = Lists.newArrayList();
        counts.addAll(documentCountByPartition.values());

        return calculateH(counts);
    }

    static int calculateWorstCaseH(int clusterSize, int partitionCount)
    {
        final ArrayList<Integer> counts = Lists.newArrayList();

        for (int partition = 0; partition < partitionCount; partition++)
        {
            counts.add(clusterSize / partitionCount
                + (partition < (clusterSize % partitionCount) ? 1 : 0));
        }

        return calculateH(counts);
    }

    static int calculateH(final ArrayList<Integer> counts)
    {
        int h = 0;
        for (int i = 0; i < counts.size() - 1; i++)
        {
            for (int j = i + 1; j < counts.size(); j++)
            {
                h += counts.get(i) * counts.get(j);
            }
        }
        return h;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
