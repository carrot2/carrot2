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
package org.carrot2.text.preprocessing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;
import org.junit.Test;

/** Test cases for {@link DocumentAssigner}. */
public class DocumentAssignerTest extends LabelFilterTestBase {
  /** Document assigner under tests */
  private DocumentAssigner documentAssigner = new DocumentAssigner();

  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.stopWordLabelFilter = new StopWordLabelFilter();
    filterProcessor.completeLabelFilter = new CompleteLabelFilter();
  }

  @Test
  public void testEmpty() {
    final int[][] expectedDocumentIndices = new int[][] {};
    check(Stream.empty(), expectedDocumentIndices, -1);
  }

  @Test
  public void testSingleWordLabels() {
    final int[][] expectedDocumentIndices = {{0}, {1}};

    documentAssigner.minClusterSize.set(1);
    check(
        Stream.of(new TestDocument("coal is", "coal is"), new TestDocument("mining", "mining")),
        expectedDocumentIndices,
        -1);
  }

  @Test
  public void testStemmedSingleWordLabelConflation() {
    final int[][] expectedDocumentIndices = {{0, 1, 2, 3}};

    documentAssigner.minClusterSize.set(1);
    check(
        Stream.of(
            new TestDocument("cat", "cat"),
            new TestDocument("cat", "cat"),
            new TestDocument("cats", "cats"),
            new TestDocument("cats", "cats")),
        expectedDocumentIndices,
        -1);
  }

  @Test
  public void testStemmedPhraseLabelConflation() {
    final int[][] expectedDocumentIndices = {
      {0, 1, 2, 3},
      {0, 1, 2, 3},
      {0, 1, 2, 3}
    };

    documentAssigner.minClusterSize.set(1);
    check(
        Stream.of(
            new TestDocument("cat horse", "cat horse"),
            new TestDocument("cats horse", "cats horse"),
            new TestDocument("cat horses", "cat horses"),
            new TestDocument("cats horses", "cats horses")),
        expectedDocumentIndices,
        2);
  }

  @Test
  public void testMinClusterSize() {
    final int[][] expectedDocumentIndices = {
      {0, 1},
      {0, 1},
      {0, 1},
      {0, 1}
    };

    documentAssigner.minClusterSize.set(2);
    check(
        Stream.of(
            new TestDocument("test coal", "test coal"),
            new TestDocument("coal test . mining", "coal test . mining")),
        expectedDocumentIndices,
        2);
  }

  @Test
  public void testPhraseLabelsExactMatch() {
    final int[][] expectedDocumentIndices = {{0, 1}};

    documentAssigner.exactPhraseAssignment.set(true);
    documentAssigner.minClusterSize.set(2);
    check(
        Stream.of(
            new TestDocument("data is cool", "data is cool"),
            new TestDocument("data is cool", "data is cool"),
            new TestDocument("data cool", "data cool")),
        expectedDocumentIndices,
        0);
  }

  @Test
  public void testPhraseLabelsNonExactMatch() {
    final int[][] expectedDocumentIndices = {
      {0, 1, 2},
      {0, 1, 2}
    };

    documentAssigner.exactPhraseAssignment.set(false);
    documentAssigner.minClusterSize.set(2);
    check(
        Stream.of(
            new TestDocument("data is cool", "data is cool"),
            new TestDocument("data is cool", "data is cool"),
            new TestDocument("data cool", "data cool")),
        expectedDocumentIndices,
        0);
  }

  @Test
  public void testPhraseLabelsNonExactMatchOtherLabels() {
    final int[][] expectedDocumentIndices = {
      {0, 1, 2},
      {0, 1, 2},
      {0, 1, 2},
      {0, 1},
      {0, 1}
    };

    check(
        Stream.of(
            new TestDocument("aa bb cc dd", "aa bb cc dd"),
            new TestDocument("dd . cc . bb . aa", "dd . cc . bb . aa"),
            new TestDocument("cc . bb . aa", "aa . bb . cc")),
        expectedDocumentIndices,
        4);
  }

  private void check(
      Stream<? extends Document> documents,
      int[][] expectedDocumentIndices,
      int expectedFirstPhraseIndex) {
    LanguageComponents comp = CachedLangComponents.loadCached("English");
    PreprocessingContext context = runPreprocessing(documents, comp);
    documentAssigner.assign(context);

    assertThat(context.allLabels.firstPhraseIndex)
        .as("allLabels.firstPhraseIndex")
        .isEqualTo(expectedFirstPhraseIndex);
    assertThat(context.allLabels.documentIndices)
        .as("allLabels.documentIndices")
        .hasSize(expectedDocumentIndices.length);
    for (int i = 0; i < expectedDocumentIndices.length; i++) {
      assertThat(context.allLabels.documentIndices[i].asIntLookupContainer().toArray())
          .as("allLabels.documentIndices[" + i + "]")
          .isEqualTo(expectedDocumentIndices[i]);
    }
  }
}
