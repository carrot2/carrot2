
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
import org.carrot2.util.MathUtils;
import org.fest.assertions.Delta;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class PrecisionRecallMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testEmptyCluster()
    {
        check(null, null, null, new Cluster());
    }

    @Test
    public void testTrivialCluster()
    {
        check(1.0, 1.0, 1.0, new Cluster("test", documentWithPartitions("test")));
    }

    @Test
    public void testPartiallyContaminatedCluster()
    {
        check((3 * 0.75 + 1 * 0.25) / 4, 1.0,
            (3 * MathUtils.harmonicMean(0.75, 1.0) + 1 * MathUtils
                .harmonicMean(0.25, 1.0)) / 4, partiallyContaminatedCluster());
    }

    @Test
    public void testFullyContaminatedCluster()
    {
        check(0.25, 1.0, 2 * 0.25 / (1 + 0.25), fullyContaminatedCluster());
    }

    @Test
    public void testPureCluster()
    {
        check(1.0, 1.0, 1.0, pureCluster());
    }

    @Test
    public void testHardClustersWithOverlappingPartitions()
    {
        check(1.0, MathUtils.arithmeticMean(2.0 / 3.0, 1, 3, 2), MathUtils
            .arithmeticMean(MathUtils.harmonicMean(2.0 / 3.0, 1), 1, 3, 2),
            hardClustersWithOverlappingPartitions());
    }

    @Test
    public void testHardPartitionsOverlappingClusters()
    {
        check(MathUtils.arithmeticMean(2.0 / 3.0, 1, 2, 2), 1.0, MathUtils
            .arithmeticMean(MathUtils.harmonicMean(2.0 / 3.0, 1), 1, 2, 2),
            overlappingClustersWithHardPartitions());
    }

    @Test
    public void testOverlappingPartitionsOverlappingClusters()
    {
        check(1.0, 1.0, 1.0, overlappingClustersWithOverlappingPartitions());
    }

    @Test
    public void testAllDocumentsInOtherTopics()
    {
        final Cluster otherTopics = clusterWithPartitions("t1", "t2", "t3");
        otherTopics.setOtherTopics(true);
        check(0.0, 0.0, 0.0, otherTopics);
    }

    @Test
    public void testIdealClustering()
    {
        check(1.0, 1.0, 1.0, idealClusters());
    }

    private void check(Double expectedAveragePrecision, Double expectedAverageRecall,
        Double expectedAverageFMeasure, Cluster... clusters)
    {
        final PrecisionRecallMetric metric = new PrecisionRecallMetric();
        metric.documents = getAllDocuments(clusters);
        metric.clusters = Lists.newArrayList(clusters);
        metric.calculate();
        assertEquals(expectedAveragePrecision, metric.weightedAveragePrecision, 0.001,
            "precision");
        assertEquals(expectedAverageRecall, metric.weightedAverageRecall, 0.001, "recall");
        assertEquals(expectedAverageFMeasure, metric.weightedAverageFMeasure, 0.001,
            "f-measure");
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
