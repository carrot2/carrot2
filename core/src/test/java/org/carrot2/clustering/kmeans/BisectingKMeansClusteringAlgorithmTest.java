/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering.kmeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.AwaitsFix;
import org.carrot2.clustering.*;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant1;
import org.junit.Test;

public class BisectingKMeansClusteringAlgorithmTest
    extends ClusteringAlgorithmTestBase<BisectingKMeansClusteringAlgorithm> {
  @Override
  protected BisectingKMeansClusteringAlgorithm algorithm() {
    return new BisectingKMeansClusteringAlgorithm();
  }

  @Test
  public void smokeTest() {
    final List<TestDocument> documents =
        Arrays.asList(
            new TestDocument("WordA . WordA"),
            new TestDocument("WordB . WordB"),
            new TestDocument("WordC . WordC"),
            new TestDocument("WordA . WordA"),
            new TestDocument("WordB . WordB"),
            new TestDocument("WordC . WordC"));

    BisectingKMeansClusteringAlgorithm algorithm = new BisectingKMeansClusteringAlgorithm();
    algorithm.labelCount.set(1);
    algorithm.partitionCount.set(3);

    final List<Cluster<TestDocument>> clusters =
        algorithm.cluster(
            documents.stream(),
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant1.NAME));

    assertNotNull(clusters);
    assertEquals(3, clusters.size());
    Assertions.assertThat(clusters.get(0).getLabels()).containsExactly("WordA");
    Assertions.assertThat(clusters.get(1).getLabels()).containsExactly("WordB");
    Assertions.assertThat(clusters.get(2).getLabels()).containsExactly("WordC");
  }

  // TODO: CARROT-1195 (clustering not deterministic)
  @AwaitsFix("https://issues.carrot2.org/browse/CARROT-1195")
  @Override
  public void testResultsStableFromRandomShuffle() throws Exception {
    super.testResultsStableFromRandomShuffle();
  }
}
