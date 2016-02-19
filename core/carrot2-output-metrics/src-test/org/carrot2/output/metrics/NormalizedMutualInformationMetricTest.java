
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
import org.fest.assertions.Delta;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class NormalizedMutualInformationMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testEmptyCluster()
    {
        check(null, new Cluster());
    }

    @Test
    public void testTrivialCluster()
    {
        check(0.0, new Cluster("test", documentWithPartitions("test")));
    }

    @Test
    public void testPartiallyContaminatedCluster()
    {
        check(0.0, partiallyContaminatedCluster());
    }

    @Test
    public void testFullyContaminatedCluster()
    {
        check(0.0, fullyContaminatedCluster());
    }

    @Test
    public void testPureCluster()
    {
        check(0.0, pureCluster());
    }

    @Test
    public void testHardClustersWithOverlappingPartitions()
    {
        check(0.61975, hardClustersWithOverlappingPartitions());
    }

    @Test
    public void testHardPartitionsOverlappingClusters()
    {
        check(0.61975, overlappingClustersWithHardPartitions());
    }

    @Test
    public void testOverlappingPartitionsOverlappingClusters()
    {
        check(0.63948, overlappingClustersWithOverlappingPartitions());
    }
    
    @Test
    public void testIdealClustering()
    {
        check(1.0, idealClusters());
    }

    @Test
    public void testAllDocumentsInOtherTopics()
    {
        final Cluster otherTopics = clusterWithPartitions("t1", "t2", "t3");
        otherTopics.setOtherTopics(true);
        check(0.0, otherTopics);
    }

    private void check(Double expectedNormalizedMutualInformation, Cluster... clusters)
    {
        final NormalizedMutualInformationMetric metric = new NormalizedMutualInformationMetric();
        metric.documents = getAllDocuments(clusters);
        metric.clusters = Lists.newArrayList(clusters);
        metric.calculate();
        assertEquals(expectedNormalizedMutualInformation, metric.normalizedMutualInformation, 0.001,
            "normalizedMutualInformation");
    }

    private static void assertEquals(Double expected, Double actual, double delta, String as)
    {
        if (expected != null)
        {
            assertThat(actual).as(as).isEqualTo(expected, Delta.delta(delta));
        }
        else
        {
            assertThat((Object) actual).as(as).isEqualTo(expected);
        }
    }

    @Override
    protected String [] getClusterMetricKeys()
    {
        return new String []
        {
            PrecisionRecallMetric.BEST_F_MEASURE_PARTITION
        };
    }
}
