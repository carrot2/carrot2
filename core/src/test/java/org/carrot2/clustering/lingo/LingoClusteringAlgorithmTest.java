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
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.AwaitsFix;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithmTestBase;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.SampleDocumentData;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.RegExpLabelFilter;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant1;
import org.carrot2.language.WordListFilter;
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
            .map(TestDocument::new);

    List<Cluster<Document>> clusters =
        algorithm.cluster(documents, CachedLangComponents.loadCached("English"));

    Assertions.assertThat(clusterLabels(clusters)).containsOnly("program");
  }

  @Test
  public void testRequestWordFilters() {
    // Set 'bar' to be a stop word. so that neither 'foo bar'
    // nor 'bar' on its own forms a cluster label.
    Stream<Document> documents =
        Stream.of("foo bar", "bar", "baz", "foo bar").map(TestDocument::new);

    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.preprocessing.phraseDfThreshold.set(1);
    algorithm.preprocessing.wordDfThreshold.set(1);

    algorithm.dictionaries.wordFilters.set(List.of(new WordListFilter("bar")));

    List<Cluster<Document>> clusters =
        algorithm.cluster(
            documents,
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant1.NAME));

    Assertions.assertThat(clusterLabels(clusters)).containsOnly("foo");

    System.out.println(Attrs.toJson(algorithm.dictionaries, AliasMapper.SPI_DEFAULTS));
  }

  @Test
  public void testRequestLabelFilters() {
    // Set 'bar' to be a stop label.
    Stream<Document> documents =
        Stream.of("foo bar", "bar", "baz", "foo bar").map(TestDocument::new);

    LingoClusteringAlgorithm algorithm = algorithm();
    algorithm.preprocessing.phraseDfThreshold.set(1);
    algorithm.preprocessing.wordDfThreshold.set(1);
    algorithm.dictionaries.labelFilters.set(List.of(new RegExpLabelFilter("(?i)^foo bar$")));

    List<Cluster<Document>> clusters =
        algorithm.cluster(
            documents,
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant1.NAME));

    Assertions.assertThat(clusterLabels(clusters)).containsOnly("bar", "foo");
  }

  // TODO: CARROT-1195 (clustering not deterministic)
  @AwaitsFix("https://issues.carrot2.org/browse/CARROT-1195")
  @Override
  public void testResultsStableFromRandomShuffle() throws Exception {
    super.testResultsStableFromRandomShuffle();
  }
}
