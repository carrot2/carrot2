
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.kmeans;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.core.test.assertions.Carrot2CoreAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BisectingKMeansClusteringAlgorithmTest extends
    ClusteringAlgorithmTestBase<BisectingKMeansClusteringAlgorithm>
{
    @Override
    public Class<BisectingKMeansClusteringAlgorithm> getComponentClass()
    {
        return BisectingKMeansClusteringAlgorithm.class;
    }

    @Test
    public void smokeTest()
    {
        final List<Document> documents = new ArrayList<>();
        documents.add(new Document("WordA . WordA"));
        documents.add(new Document("WordB . WordB"));
        documents.add(new Document("WordC . WordC"));
        documents.add(new Document("WordA . WordA"));
        documents.add(new Document("WordB . WordB"));
        documents.add(new Document("WordC . WordC"));

        processingAttributes.put(BisectingKMeansClusteringAlgorithm.ATTR_LABEL_COUNT, 1);
        processingAttributes.put(BisectingKMeansClusteringAlgorithm.ATTR_PARTITION_COUNT, 3);
        final List<Cluster> clusters = cluster(documents).getClusters();

        assertNotNull(clusters);
        assertEquals(3, clusters.size());
        Carrot2CoreAssertions.assertThat(clusters.get(0)).hasLabel("WordA");
        Carrot2CoreAssertions.assertThat(clusters.get(1)).hasLabel("WordB");
        Carrot2CoreAssertions.assertThat(clusters.get(2)).hasLabel("WordC");
    }
}
