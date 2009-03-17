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

package org.carrot2.output.metrics;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class ContaminationMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testCalculateWorstCaseH()
    {
        assertThat(ContaminationMetric.calculateWorstCaseH(0, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(1, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(2, 1)).isEqualTo(0);
        assertThat(ContaminationMetric.calculateWorstCaseH(2, 2)).isEqualTo(1);
        assertThat(ContaminationMetric.calculateWorstCaseH(8, 4)).isEqualTo(24);
        assertThat(ContaminationMetric.calculateWorstCaseH(6, 4)).isEqualTo(13);
    }

    @Test
    public void testCalculateContaminationEmptyCluster()
    {
        check(new Cluster(), null);
    }

    @Test
    public void testCalculateContaminationTrivialCluster()
    {
        check(new Cluster("test", documentWithTopic("test")), 0.0);
    }

    @Test
    public void testCalculateContaminationPureCluster()
    {
        check(pureCluster(), 0.0);
    }

    @Test
    public void testCalculateContaminationPartiallyContaminatedCluster()
    {
        check(partiallyContaminatedCluster(), 0.75);
    }

    @Test
    public void testCalculateContaminationFullyContaminatedCluster()
    {
        check(fullyContaminatedCluster(), 1.0);
    }

    private void check(Cluster cluster, Double expectedContamination)
    {
        final ContaminationMetric metric = new ContaminationMetric();
        metric.documents = cluster.getAllDocuments();
        metric.clusters = Lists.newArrayList(cluster);
        metric.calculate();
        assertThat(cluster.<Double> getAttribute(ContaminationMetric.CONTAMINATION))
            .isEqualTo(expectedContamination);
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
