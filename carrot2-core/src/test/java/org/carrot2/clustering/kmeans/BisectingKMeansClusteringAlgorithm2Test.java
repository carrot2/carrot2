
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
import org.carrot2.core.test.assertions.Carrot2CoreAssertions;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.LanguageModels;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BisectingKMeansClusteringAlgorithm2Test extends ClusteringAlgorithmTestBase
{
    @Test
    public void smokeTest()
    {
        final List<Document> documents = Arrays.asList(
            new Document("WordA . WordA"),
            new Document("WordB . WordB"),
            new Document("WordC . WordC"),
            new Document("WordA . WordA"),
            new Document("WordB . WordB"),
            new Document("WordC . WordC"));

        BisectingKMeansClusteringAlgorithm2 algorithm = new BisectingKMeansClusteringAlgorithm2();
        algorithm.labelCount.set(1);
        algorithm.partitionCount.set(3);

        LanguageModel languageModel = LanguageModels.english();
        final List<Cluster> clusters = algorithm.cluster(documents, languageModel);

        assertNotNull(clusters);
        assertEquals(3, clusters.size());
        Carrot2CoreAssertions.assertThat(clusters.get(0)).hasLabel("WordA");
        Carrot2CoreAssertions.assertThat(clusters.get(1)).hasLabel("WordB");
        Carrot2CoreAssertions.assertThat(clusters.get(2)).hasLabel("WordC");
    }

    @Override
    protected BisectingKMeansClusteringAlgorithm2 algorithm() {
        return new BisectingKMeansClusteringAlgorithm2();
    }
}

