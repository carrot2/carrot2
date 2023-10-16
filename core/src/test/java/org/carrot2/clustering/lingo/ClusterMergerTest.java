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
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/** Test cases for cluster merging in {@link ClusterBuilder}. */
public class ClusterMergerTest extends LingoProcessingComponentTestBase {
  /** Label builder under tests */
  private ClusterBuilder clusterBuilder;

  @Before
  public void setUpClusterLabelBuilder() {
    clusterBuilder = new ClusterBuilder();
    clusterBuilder.labelAssigner = new SimpleLabelAssigner();
    reducer = new TermDocumentMatrixReducer();
    reducer.factorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
    desiredClusterCountBase = 25;
  }

  @Test
  public void testEmpty() {
    check(Stream.empty(), new int[0][]);
  }

  @Test
  public void testNoMerge() {
    final int[][] expectedDocumentIndices = {
      {0, 2},
      {0, 1},
      {1, 2}
    };

    desiredClusterCountBase = 30;
    check(
        Stream.of(
            new TestDocument("", "aa . bb"),
            new TestDocument("", "bb . cc"),
            new TestDocument("", "cc . aa")),
        expectedDocumentIndices);
  }

  @Test
  public void testSimpleMerge() {

    desiredClusterCountBase = 20;
    clusterBuilder.phraseLabelBoost.set(0.08);
    clusterBuilder.clusterMergingThreshold.set(0.4);
    preprocessingPipeline.labelFilters.minLengthLabelFilter = null;

    final int[][] expectedDocumentIndices = {{0, 1}, null};

    check(
        Stream.of(new TestDocument("aa", "aa"), new TestDocument("aa bb", "aa bb")),
        expectedDocumentIndices);
  }

  @Test
  public void testMultiMerge() {
    preprocessingPipeline.documentAssigner.minClusterSize.set(2);
    desiredClusterCountBase = 20;
    clusterBuilder.phraseLabelBoost.set(0.05);
    clusterBuilder.clusterMergingThreshold.set(0.2);
    preprocessingPipeline.labelFilters.minLengthLabelFilter = null;
    preprocessingPipeline.labelFilters.completeLabelFilter = null;

    final int[][] expectedDocumentIndices = {
      {3, 4}, {0, 1, 2}, null, null,
    };

    check(
        Stream.of(
            new TestDocument("aa", "aa"),
            new TestDocument("aa bb", "aa bb"),
            new TestDocument("aa bb cc", "aa bb cc"),
            new TestDocument("dd dd", "dd dd"),
            new TestDocument("dd dd", "dd dd")),
        expectedDocumentIndices);
  }

  private void check(Stream<? extends Document> documents, int[][] expectedDocumentIndices) {
    buildLingoModel(documents);

    final TfTermWeighting termWeighting = new TfTermWeighting();
    clusterBuilder.buildLabels(lingoContext, termWeighting);
    clusterBuilder.assignDocuments(lingoContext);
    clusterBuilder.merge(lingoContext);

    for (int i = 0; i < expectedDocumentIndices.length; i++) {
      final String description = "clusterDocuments[" + i + "]";
      if (expectedDocumentIndices[i] != null) {
        Assertions.assertThat(lingoContext.clusterDocuments[i]).as(description).isNotNull();
        Assertions.assertThat(lingoContext.clusterDocuments[i].asIntLookupContainer().toArray())
            .as(description)
            .containsOnly(expectedDocumentIndices[i]);
      } else {
        Assertions.assertThat(lingoContext.clusterDocuments[i]).as(description).isNull();
      }
    }
  }
}
