/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering.lingo;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.AwaitsFix;
import org.carrot2.clustering.*;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.junit.Test;

public class LingoClusteringAlgorithmTest
    extends ClusteringAlgorithmTestBase<LingoClusteringAlgorithm> {
  @Override
  protected LingoClusteringAlgorithm algorithm() {
    return new LingoClusteringAlgorithm();
  }

  @Test
  public void testClusteringWithDfThreshold() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.preprocessing.wordDfThreshold.set(100);

    List<Cluster<Document>> clusters =
        algorithm.cluster(
            SampleDocumentData.DOCUMENTS_DATA_MINING.stream(),
            CachedLangComponents.loadCached("English"));

    // Clustering with df threshold must not fail
    Assertions.assertThat(clusters).isEmpty();
  }

  @Test
  public void testNoLabelCandidates() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.queryHint.set("test");

    Stream<Document> documents =
        Stream.of(new TestDocument("test"), new TestDocument("test"), new TestDocument("test"));

    List<Cluster<Document>> clusters =
        algorithm.cluster(documents, CachedLangComponents.loadCached("English"));

    Assertions.assertThat(clusters).isEmpty();
  }

  @Test
  public void testStemmingUsedWithDefaultAttributes() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.queryHint.set("test");

    Stream<Document> documents =
        Stream.of(
                "program", "programs", "programming", "program", "programs", "programming", "other")
            .map(title -> new TestDocument(title));

    List<Cluster<Document>> clusters =
        algorithm.cluster(documents, CachedLangComponents.loadCached("English"));

    Assertions.assertThat(clusters).isNotEmpty();
    Assertions.assertThat(
            clusters.stream()
                .flatMap(c -> c.getLabels().stream())
                .map(label -> label.toLowerCase(Locale.ROOT)))
        .containsOnly("program");
  }

  // TODO: CARROT-1195 (clustering not deterministic)
  @AwaitsFix("https://issues.carrot2.org/browse/CARROT-1195")
  @Override
  public void testResultsStableFromRandomShuffle() throws Exception {
    super.testResultsStableFromRandomShuffle();
  }
}
