
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
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link ClusteringMetric}.
 */
public class ContaminationMetricTest
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

    @Test
    public void testNoTopicInformation()
    {
        final List<Document> documents = Lists.newArrayList();
        final List<Cluster> clusters = Lists.newArrayList();

        final ContaminationMetric metric = new ContaminationMetric();
        metric.documents = documents;

        final Document d1 = new Document();
        final Document d2 = documentWithTopic("test");
        documents.add(d1);

        final Cluster c1 = new Cluster("test", d1);
        clusters.add(c1);

        metric.calculate();
        assertThat(c1.getAttribute(ContaminationMetric.CONTAMINATION)).isNull();

        documents.add(d2);
        assertThat(c1.getAttribute(ContaminationMetric.CONTAMINATION)).isNull();
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

    private Cluster pureCluster()
    {
        final Document d1 = documentWithTopic("test");
        final Document d2 = documentWithTopic("test");
        final Document d3 = documentWithTopic("test");
        final Document d4 = documentWithTopic("test");
        final Cluster cluster = new Cluster("test", d1, d2, d3, d4);
        return cluster;
    }

    private Cluster partiallyContaminatedCluster()
    {
        final Document d1 = documentWithTopic("test1");
        final Document d2 = documentWithTopic("test1");
        final Document d3 = documentWithTopic("test1");
        final Document d4 = documentWithTopic("test2");
        final Cluster cluster = new Cluster("test", d1, d2, d3, d4);
        return cluster;
    }

    private Cluster fullyContaminatedCluster()
    {
        final Document d1 = documentWithTopic("test1");
        final Document d2 = documentWithTopic("test2");
        final Document d3 = documentWithTopic("test3");
        final Document d4 = documentWithTopic("test4");
        final Cluster cluster = new Cluster("test", d1, d2, d3, d4);
        return cluster;
    }

    private Document documentWithTopic(final String topic)
    {
        final Document document = new Document();
        document.addField(Document.TOPIC, topic);
        return document;
    }
}
