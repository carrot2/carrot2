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
package org.carrot2.clustering.stc;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.assertj.core.api.Assertions;
import org.carrot2.clustering.*;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.Document;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class STCClusteringAlgorithmTest
    extends ClusteringAlgorithmTestBase<STCClusteringAlgorithm> {
  @Override
  protected STCClusteringAlgorithm algorithm() {
    return new STCClusteringAlgorithm();
  }

  // Clustering with a very high df threshold must not fail.
  @Test
  public void testClusteringWithDfThreshold() {
    STCClusteringAlgorithm algorithm = algorithm();
    algorithm.preprocessing.wordDfThreshold.set(20);

    List<Cluster<Document>> clusters =
        algorithm.cluster(
            SampleDocumentData.DOCUMENTS_DATA_MINING.stream(),
            CachedLangComponents.loadCached("English"));
    Assertions.assertThat(clusters.size()).isEqualTo(0);
  }

  @Test
  public void testMaxClusters() {
    STCClusteringAlgorithm algorithm = algorithm();
    algorithm.maxClusters.set(9);

    List<Cluster<Document>> clusters =
        algorithm.cluster(
            SampleDocumentData.DOCUMENTS_DATA_MINING.stream(),
            CachedLangComponents.loadCached("English"));

    Assertions.assertThat(clusters.size()).isEqualTo(9);
  }

  @Test
  public void testComputeIntersection() {
    int[] t1;

    t1 = new int[] {0, 1, 2, 1, 2, 3};
    Assertions.assertThat(STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3))
        .isEqualTo(2);

    t1 = new int[] {0, 1, 2, 3, 5, 6};
    Assertions.assertThat(STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3))
        .isEqualTo(0);

    t1 = new int[] {0, 1, 2, -1, 2, 6};
    Assertions.assertThat(STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 3))
        .isEqualTo(1);

    t1 = new int[] {0, 1, 2, 0};
    Assertions.assertThat(STCClusteringAlgorithm.computeIntersection(t1, 0, 3, t1, 3, 1))
        .isEqualTo(1);
  }

  @Test
  public void testMergingBaseClustersWithStemEquivalentPhrases() {
    List<String> titles =
        Arrays.asList(
            "good programs . foo1",
            "foo2 good programs . foo2",
            "good programs taste good",
            "good programs are good",
            "good programming . foo3",
            "foo4 good programming . foo4",
            "good programming makes you feel better");

    STCClusteringAlgorithm algorithm = algorithm();
    algorithm.minBaseClusterScore.set(0d);

    Stream<TestDocument> documentStream = titles.stream().map(title -> new TestDocument(title));
    List<Cluster<Document>> clusters =
        algorithm.cluster(documentStream, CachedLangComponents.loadCached("English"));

    Assertions.assertThat(clusters.stream().flatMap(cluster -> cluster.getLabels().stream()))
        .contains("Good Programs")
        .doesNotContain("Good Programming");
  }

  /** CARROT-1008: STC is not using term stems. */
  @Test
  public void testCarrot1008() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Element root;
    try (InputStream is = getClass().getResourceAsStream("CARROT-1008.xml")) {
      root = db.parse(is).getDocumentElement();
    }

    Stream<TestDocument> docStream =
        elementStream(root, "document")
            .map(
                docElement -> {
                  String title =
                      elementStream(docElement, "title")
                          .map(e -> e.getTextContent())
                          .collect(Collectors.joining(" . "));
                  String snippet =
                      elementStream(docElement, "snippet")
                          .map(e -> e.getTextContent())
                          .collect(Collectors.joining(" . "));
                  return new TestDocument(title, snippet);
                });

    STCClusteringAlgorithm algorithm = algorithm();
    algorithm.maxClusters.set(30);

    List<Cluster<Document>> clusters =
        algorithm.cluster(docStream, CachedLangComponents.loadCached("English"));

    List<String> collect =
        clusters.stream().flatMap(c -> c.getLabels().stream()).collect(Collectors.toList());
    Assertions.assertThat(collect.contains("Guns") && collect.contains("Gun")).isFalse();
  }

  @Override
  public void testResultsStableFromRandomShuffle() throws Exception {
    super.testResultsStableFromRandomShuffle();
  }

  private Stream<Element> elementStream(Element parent, String childName) {
    NodeList list = parent.getElementsByTagName(childName);
    return IntStream.range(0, list.getLength()).mapToObj(i -> (Element) list.item(i));
  }
}
