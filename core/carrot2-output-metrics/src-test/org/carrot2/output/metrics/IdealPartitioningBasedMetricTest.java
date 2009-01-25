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
 * Test cases for {@link IClusteringMetric}.
 */
public class IdealPartitioningBasedMetricTest
{
    protected String [] getClusterMetricKeys()
    {
        return new String [0];
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
        checkAllMetricsNull(c1);

        documents.add(d2);
        checkAllMetricsNull(c1);
    }

    protected Document documentWithTopic(final String topic)
    {
        final Document document = new Document();
        document.addField(Document.TOPIC, topic);
        return document;
    }

    private void checkAllMetricsNull(final Cluster c1)
    {
        final String [] clusterMetricKeys = getClusterMetricKeys();
        for (String metricKey : clusterMetricKeys)
        {
            assertThat(c1.getAttribute(metricKey)).isNull();
        }
    }
}
