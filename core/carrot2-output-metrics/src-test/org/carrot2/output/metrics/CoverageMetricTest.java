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

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.fest.assertions.DoubleAssert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Test cases for {@link CoverageMetric}
 */
public class CoverageMetricTest extends IdealPartitioningBasedMetricTest
{
    @Test
    public void testNoClusters()
    {
        check(ImmutableList.of(documentWithTopic("test")), ImmutableList.<Cluster> of(),
            0, 0, 0);
    }

    @Test
    public void testPartialCoverage()
    {
        final Document d1 = documentWithTopic("test1");
        final Document d2 = documentWithTopic("test2");
        final Document d3 = documentWithTopic("test3");
        final Document d4 = documentWithTopic("test4");

        final Cluster c1 = new Cluster("c1", d1);
        final Cluster c2 = new Cluster("c2", d2);
        final Cluster c3 = new Cluster("c3", d3);

        check(ImmutableList.of(d1, d2, d3, d4), ImmutableList.of(c1, c2, c3), 0.75, 0.75,
            0.75);
    }

    @Test
    public void testPartialAbsoluteCoverage()
    {
        final Document d1 = documentWithTopic("test1");
        final Document d2 = documentWithTopic("test2");
        final Document d22 = documentWithTopic("test2");
        final Document d3 = documentWithTopic("test3");
        final Document d4 = documentWithTopic("test4");

        final Cluster c1 = new Cluster("c1", d1);
        final Cluster c2 = new Cluster("c2", d2);
        final Cluster c22 = new Cluster("c22", d22);
        final Cluster c3 = new Cluster("c3", d3);
        final Cluster c4 = new Cluster("c4", d4);

        check(ImmutableList.of(d1, d2, d22, d3, d4), ImmutableList
            .of(c1, c2, c22, c3, c4), 0.894, 1, 1);
    }

    @Test
    public void testFullCoverage()
    {
        final Document d1 = documentWithTopic("test1");
        final Document d2 = documentWithTopic("test2");
        final Document d3 = documentWithTopic("test3");
        final Document d4 = documentWithTopic("test4");

        final Cluster c1 = new Cluster("c1", d1);
        final Cluster c2 = new Cluster("c2", d2);
        final Cluster c3 = new Cluster("c3", d3);
        final Cluster c4 = new Cluster("c4", d4);

        check(ImmutableList.of(d1, d2, d3, d4), ImmutableList.of(c1, c2, c3, c4), 1.0, 1,
            1);
    }

    private void check(List<Document> documents, List<Cluster> clusters,
        double expectedAbsoluteTopicCoverage, double expectedTopicCoverage,
        double expectedDocumentCoverage)
    {
        final CoverageMetric metric = new CoverageMetric();
        metric.documents = documents;
        metric.clusters = clusters;
        metric.calculate();
        assertThat(metric.absoluteTopicCoverage).isEqualTo(expectedAbsoluteTopicCoverage,
            DoubleAssert.delta(0.001));
        assertThat(metric.topicCoverage).isEqualTo(expectedTopicCoverage);
        assertThat(metric.documentCoverage).isEqualTo(expectedDocumentCoverage);
    }

    @Override
    protected String [] getClusterMetricKeys()
    {
        return new String [] {};
    }
}
