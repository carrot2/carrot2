
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

package org.carrot2.clustering.lingo;

import org.assertj.core.api.Assertions;
import org.carrot2.clustering.*;
import org.carrot2.language.EnglishLanguageComponentsFactory;
import org.carrot2.language.LanguageComponents;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class LingoClusteringAlgorithmTest extends ClusteringAlgorithmTestBase {
  @Override
  protected LingoClusteringAlgorithm algorithm() {
    return new LingoClusteringAlgorithm();
  }

  @Test
  public void testClusteringWithDfThreshold() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.preprocessing.get().wordDfThreshold.set(100);

    List<Cluster<Document>> clusters = algorithm.cluster(SampleDocumentData.DOCUMENTS_DATA_MINING.stream(),
        LanguageComponents.get(EnglishLanguageComponentsFactory.NAME));

    // Clustering with df threshold must not fail
    Assertions.assertThat(clusters).isEmpty();
  }

  @Test
  public void testNoLabelCandidates() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.queryHint.set("test");

    Stream<Document> documents = Stream.of(
        new TestDocument("test"),
        new TestDocument("test"),
        new TestDocument("test")
    );

    List<Cluster<Document>> clusters = algorithm.cluster(documents,
        LanguageComponents.get(EnglishLanguageComponentsFactory.NAME));

    Assertions.assertThat(clusters).isEmpty();
  }

  @Test
  public void testStemmingUsedWithDefaultAttributes() {
    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.queryHint.set("test");

    Stream<Document> documents = Stream.of(
        "program",
        "programs",
        "programming",
        "program",
        "programs",
        "programming",
        "other"
    ).map(title -> new TestDocument(title));

    List<Cluster<Document>> clusters = algorithm.cluster(documents,
        LanguageComponents.get(EnglishLanguageComponentsFactory.NAME));

    Assertions.assertThat(clusters).isNotEmpty();
    Assertions.assertThat(clusters.stream()
        .flatMap(c -> c.getLabels().stream())
        .map(label -> label.toLowerCase(Locale.ROOT)))
        .containsOnly("program");
  }
}