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
package org.carrot2.clustering.lingo;

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.math.matrix.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/** Test cases for cluster document assignment in {@link ClusterBuilder}. */
public class ClusterDocumentAssignerTest extends LingoProcessingComponentTestBase {
  /** Label builder under tests */
  private ClusterBuilder clusterBuilder;

  @Before
  public void setUpClusterLabelBuilder() {
    clusterBuilder = new ClusterBuilder();
    clusterBuilder.labelAssigner = new SimpleLabelAssigner();
    reducer.factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
  }

  @Test
  public void testEmpty() {
    check(Stream.empty(), new int[][] {});
  }

  @Test
  public void testNoPhrases() {
    desiredClusterCountBase = 30;

    final int[][] expectedDocumentIndices = {
      {0, 2},
      {0, 1},
      {1, 2}
    };

    check(
        Stream.of(
            new TestDocument("", "aa . bb"),
            new TestDocument("", "cc . bb"),
            new TestDocument("", "cc . aa")),
        expectedDocumentIndices);
  }

  @Test
  public void testSinglePhraseNoSingleWords() {
    desiredClusterCountBase = 10;

    final int[][] expectedDocumentIndices = {{0, 1}};

    check(
        Stream.of(new TestDocument("aa bb", "aa bb"), new TestDocument("aa bb", "aa bb")),
        expectedDocumentIndices);
  }

  @Test
  public void testSinglePhraseSingleWords() {
    desiredClusterCountBase = 15;
    clusterBuilder.phraseLabelBoost.set(0.3);

    final int[][] expectedDocumentIndices = {
      {0, 2},
      {1, 2}
    };

    check(
        Stream.of(
            new TestDocument("aa bb", "aa bb"),
            new TestDocument("cc", "cc"),
            new TestDocument("aa bb", "aa bb . cc")),
        expectedDocumentIndices);
  }

  private void check(Stream<? extends Document> documents, int[][] expectedDocumentIndices) {
    buildLingoModel(documents);

    final TfTermWeighting termWeighting = new TfTermWeighting();
    clusterBuilder.buildLabels(lingoContext, termWeighting);
    clusterBuilder.assignDocuments(lingoContext);

    for (int i = 0; i < expectedDocumentIndices.length; i++) {
      Assertions.assertThat(lingoContext.clusterDocuments[i].asIntLookupContainer().toArray())
          .as("clusterDocuments[" + i + "]")
          .containsOnly(expectedDocumentIndices[i]);
    }
  }
}
