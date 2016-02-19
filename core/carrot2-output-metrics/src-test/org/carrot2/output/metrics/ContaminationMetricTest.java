
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

import org.carrot2.core.Cluster;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class ContaminationMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testWorstCaseH()
    {
        assertThat(ContaminationMetric.calculateWorstCaseH(0, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(1, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(2, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(2, 2)).isEqualTo(1);
        assertThat(ContaminationMetric.calculateWorstCaseH(8, 4)).isEqualTo(24);
        assertThat(ContaminationMetric.calculateWorstCaseH(6, 4)).isEqualTo(13);
    }

    @Test
    public void testEmptyCluster()
    {
        check(new Cluster(), null);
    }

    @Test
    public void testTrivialCluster()
    {
        check(new Cluster("test", documentWithPartitions("test")), 0.0);
    }

    @Test
    public void testPureCluster()
    {
        check(pureCluster(), 0.0);
    }

    @Test
    public void testPartiallyContaminatedCluster()
    {
        check(partiallyContaminatedCluster(), 0.75);
    }

    @Test
    public void testFullyContaminatedCluster()
    {
        check(fullyContaminatedCluster(), 1.0);
    }

    @Test
    public void testHardClustersWithOverlappingPartitions()
    {
        // Second cluster is fully contaminated even though it perfectly matches 
        // second partition. This is because the partition itself is "contaminated"
        // by sharing one document with the first partition.
        check(hardClustersWithOverlappingPartitions(), 0.0, 1.0);
    }
    @Test
    public void testHardPartitionsOverlappingClusters()
    {
        check(overlappingClustersWithHardPartitions(), 1.0, 0.0);
    }

    @Test
    public void testOverlappingPartitionsOverlappingClusters()
    {
        // Again, clusters are penalized because partitions themselves are
        // "contaminated", see comment above.
        check(overlappingClustersWithOverlappingPartitions(), 0.75, 1.0);
    }

    @Test
    public void testAllDocumentsInOtherTopics()
    {
        final Cluster otherTopics = clusterWithPartitions("t1", "t2", "t3");
        otherTopics.setOtherTopics(true);
        check(otherTopics, null);
    }

    @Test
    public void testIdealClustering()
    {
        check(idealClusters(), 0.0, 0.0);
    }

    private void check(Cluster cluster, Double expectedContamination)
    {
        check(new Cluster []
        {
            cluster
        }, expectedContamination);
    }

    private void check(Cluster [] clusters, Double... expectedContaminations)
    {
        final ContaminationMetric metric = new ContaminationMetric();
        metric.documents = getAllDocuments(clusters);
        metric.clusters = Lists.newArrayList(clusters);
        metric.calculate();
        for (int i = 0; i < clusters.length; i++)
        {
            assertThat(
                clusters[i].<Object> getAttribute(ContaminationMetric.CONTAMINATION))
                .isEqualTo(expectedContaminations[i]);
        }
    }

    @Override
    protected String [] getClusterMetricKeys()
    {
        return new String []
        {
            ContaminationMetric.CONTAMINATION
        };
    }
}
