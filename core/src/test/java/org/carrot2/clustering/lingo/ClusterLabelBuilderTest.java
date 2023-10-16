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
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.TfTermWeighting;
import org.junit.Before;
import org.junit.Test;

/** Test cases for label building in {@link ClusterBuilder}. */
public class ClusterLabelBuilderTest extends LingoProcessingComponentTestBase {
  /** Label builder under tests */
  private ClusterBuilder clusterBuilder;

  @Before
  public void setUpClusterLabelBuilder() {
    clusterBuilder = new ClusterBuilder();
    clusterBuilder.labelAssigner = new SimpleLabelAssigner();
    reducer = new TermDocumentMatrixReducer();
  }

  @Test
  public void testEmpty() {
    buildModelAndCheck(Stream.empty(), new int[0]);
  }

  @Test
  public void testNoPhrases() {
    final int[] expectedFeatureIndex = {0, 1, 2};

    desiredClusterCountBase = 30;
    buildModelAndCheck(
        Stream.of(
            new TestDocument("", "aa . bb"),
            new TestDocument("", "bb . cc"),
            new TestDocument("", "cc . aa")),
        expectedFeatureIndex);
  }

  @Test
  public void testSinglePhraseNoSingleWords() {
    final int[] expectedFeatureIndex = {2};

    desiredClusterCountBase = 10;
    buildModelAndCheck(
        Stream.of(new TestDocument("aa bb", "aa bb"), new TestDocument("aa bb", "aa bb")),
        expectedFeatureIndex);
  }

  @Test
  public void testSinglePhraseSingleWords() {
    final int[] expectedFeatureIndex = {2, 3};

    clusterBuilder.phraseLabelBoost.set(0.5);
    desiredClusterCountBase = 15;
    buildModelAndCheck(
        Stream.of(
            new TestDocument("aa bb", "aa bb"),
            new TestDocument("cc", "cc"),
            new TestDocument("aa bb", "aa bb . cc")),
        expectedFeatureIndex);
  }

  @Test
  public void testQueryWordsRemoval() {
    final int[] expectedFeatureIndex = {0};

    clusterBuilder.phraseLabelBoost.set(0.5);
    desiredClusterCountBase = 10;
    queryHint = "query word";

    buildModelAndCheck(
        Stream.of(
            new TestDocument("query word . aa", "query word . aa"),
            new TestDocument("query . word", "query . word . aa")),
        expectedFeatureIndex);
  }

  @Test
  public void testExternalFeatureScores() {
    clusterBuilder.phraseLabelBoost.set(0.5);
    desiredClusterCountBase = 15;

    final int[] expectedFeatureIndex = {6, 7, 2, 3};

    buildModelAndCheck(
        Stream.of(
            new TestDocument("aa bb", "aa bb"),
            new TestDocument("cc", "cc"),
            new TestDocument("cc", "cc"),
            new TestDocument("aa bb", "aa bb"),
            new TestDocument("dd", "dd"),
            new TestDocument("dd", "dd"),
            new TestDocument("ee ff", "ee ff"),
            new TestDocument("ee ff", "ee ff")),
        expectedFeatureIndex);

    // Make a copy of feature indices
    final int[] featureIndex = lingoContext.preprocessingContext.allLabels.featureIndex;

    for (int i = 0; i < featureIndex.length; i++) {
      clusterBuilder.featureScorer = new OneLabelFeatureScorer(i, 2);
      check(new int[] {featureIndex[i], featureIndex[i], featureIndex[i], featureIndex[i]});
    }
  }

  private static class OneLabelFeatureScorer implements FeatureScorer {
    private int labelIndex;
    private double score;

    OneLabelFeatureScorer(int labelIndex, double score) {
      this.labelIndex = labelIndex;
      this.score = score;
    }

    public double[] getFeatureScores(LingoProcessingContext lingoContext) {
      final double[] scores =
          new double[lingoContext.preprocessingContext.allLabels.featureIndex.length];
      scores[labelIndex] = score;
      return scores;
    }
  }

  private void buildModelAndCheck(
      Stream<? extends Document> documents, int[] expectedFeatureIndex) {
    buildLingoModel(documents);
    check(expectedFeatureIndex);
  }

  private void check(int[] expectedFeatureIndex) {
    clusterBuilder.buildLabels(lingoContext, new TfTermWeighting());
    Assertions.assertThat(lingoContext.clusterLabelFeatureIndex)
        .as("clusterLabelFeatureIndex")
        .containsOnly(expectedFeatureIndex);
  }
}
