
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
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Test cases for {@link IClusteringMetric}.
 */
public class IdealPartitioningBasedMetricTest extends CarrotTestCase
{
    @Test
    public void testNoPartitionInformation()
    {
        final List<Document> documents = Lists.newArrayList();
        final List<Cluster> clusters = Lists.newArrayList();

        final ContaminationMetric metric = new ContaminationMetric();
        metric.documents = documents;

        final Document d1 = new Document();
        final Document d2 = documentWithPartitions("test");
        documents.add(d1);

        final Cluster c1 = new Cluster("test", d1);
        clusters.add(c1);

        metric.calculate();
        checkAllMetricsNull(c1);

        documents.add(d2);
        checkAllMetricsNull(c1);
    }

    private void checkAllMetricsNull(final Cluster c1)
    {
        final String [] clusterMetricKeys = getClusterMetricKeys();
        for (String metricKey : clusterMetricKeys)
        {
            assertThat((Object) c1.getAttribute(metricKey)).isNull();
        }
    }

    protected String [] getClusterMetricKeys()
    {
        return new String [0];
    }

    protected static List<Document> getAllDocuments(Cluster... clusters)
    {
        final Set<Document> set = Sets.newHashSet();
        for (Cluster cluster : clusters)
        {
            set.addAll(cluster.getAllDocuments());
        }
        return Lists.newArrayList(set);
    }

    protected Document documentWithPartitions(final String... partitions)
    {
        final Document document = new Document();
        document.setField(Document.PARTITIONS, Arrays.asList(partitions));
        return document;
    }

    protected Cluster clusterWithPartitions(final String... partitions)
    {
        final Cluster cluster = new Cluster();

        for (String partition : partitions)
        {
            cluster.addDocuments(documentWithPartitions(partition));
        }

        return cluster;
    }

    protected Cluster pureCluster()
    {
        return clusterWithPartitions("test", "test", "test", "test");
    }

    protected Cluster partiallyContaminatedCluster()
    {
        return clusterWithPartitions("test1", "test1", "test1", "test2");
    }

    protected Cluster fullyContaminatedCluster()
    {
        return clusterWithPartitions("test1", "test2", "test3", "test4");
    }

    protected Cluster [] idealClusters()
    {
        return new Cluster []
        {
            clusterWithPartitions("t1", "t1", "t1"), clusterWithPartitions("t2", "t2")
        };
    }

    protected Cluster [] hardClustersWithOverlappingPartitions()
    {
        final Cluster c1 = new Cluster("c1", documentWithPartitions("t1"),
            documentWithPartitions("t1"));
        final Cluster c2 = new Cluster("c2", documentWithPartitions("t1", "t2"),
            documentWithPartitions("t2"));
        return new Cluster []
        {
            c1, c2
        };
    }

    protected Cluster [] overlappingClustersWithHardPartitions()
    {
        final Document document = documentWithPartitions("t2");
        final Cluster c1 = new Cluster("c1", documentWithPartitions("t1"),
            documentWithPartitions("t1"), document);
        final Cluster c2 = new Cluster("c2", document, documentWithPartitions("t2"));
        return new Cluster []
        {
            c1, c2
        };
    }

    protected Cluster [] overlappingClustersWithOverlappingPartitions()
    {
        final Document documentWithTwoPartitions = documentWithPartitions("t1", "t2");
        final Cluster c1 = new Cluster("c1", documentWithPartitions("t1"),
            documentWithPartitions("t1"), documentWithTwoPartitions);
        final Cluster c2 = new Cluster("c2", documentWithTwoPartitions,
            documentWithPartitions("t2"));
        return new Cluster []
        {
            c1, c2
        };
    }
}
