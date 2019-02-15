
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

import org.assertj.core.api.Assertions;
import org.carrot2.AbstractTest;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactory;
import org.carrot2.util.attrs.Attrs;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BisectingKMeansClusteringAlgorithmTest extends AbstractTest {

  @Test
  public void smokeTest() {
    final List<TestDocument> documents = Arrays.asList(
        new TestDocument("WordA . WordA"),
        new TestDocument("WordB . WordB"),
        new TestDocument("WordC . WordC"),
        new TestDocument("WordA . WordA"),
        new TestDocument("WordB . WordB"),
        new TestDocument("WordC . WordC"));

    BisectingKMeansClusteringAlgorithm algorithm = new BisectingKMeansClusteringAlgorithm();
    algorithm.labelCount.set(1);
    algorithm.partitionCount.set(3);

    final List<Cluster<TestDocument>> clusters = algorithm.cluster(documents.stream(),
        LanguageComponents.get(TestsLanguageComponentsFactory.NAME));

    assertNotNull(clusters);
    assertEquals(3, clusters.size());
    Assertions.assertThat(clusters.get(0).getLabels()).containsExactly("WordA");
    Assertions.assertThat(clusters.get(1).getLabels()).containsExactly("WordB");
    Assertions.assertThat(clusters.get(2).getLabels()).containsExactly("WordC");
  }
}