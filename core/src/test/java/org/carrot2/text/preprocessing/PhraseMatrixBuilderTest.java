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

import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.math.mahout.matrix.DoubleMatrix2D;
import org.carrot2.math.matrix.MatrixAssertions;
import org.junit.Test;

/** Test cases for phrase matrix building. */
public class PhraseMatrixBuilderTest extends TermDocumentMatrixBuilderTestBase {
  @Test
  public void testEmpty() {
    check(Stream.empty(), null);
  }

  @Test
  public void testNoPhrases() {
    Stream<TestDocument> documents =
        createDocumentsWithTitles("aa . bb", "bb . cc", "aa . cc . cc");
    check(documents, null);
  }

  @Test
  public void testSinglePhraseNoSingleWords() {
    Stream<TestDocument> documents = createDocumentsWithTitles("aa bb cc", "aa bb cc", "aa bb cc");
    double[][] expectedPhraseMatrixElements = {{0.577, 0.577, 0.577}};
    check(documents, expectedPhraseMatrixElements);
  }

  @Test
  public void testTwoPhrasesNoSingleWords() {
    double[][] expectedPhraseMatrixElements = {
      {0.707, 0.707, 0, 0, 0},
      {0, 0, 0.577, 0.577, 0.577}
    };

    check(
        Stream.of(
            new TestDocument("ee ff", "aa bb cc"),
            new TestDocument("ee ff", "aa bb cc"),
            new TestDocument("ee ff", "aa bb cc")),
        expectedPhraseMatrixElements);
  }

  @Test
  public void testSinglePhraseSingleWords() {
    Stream<TestDocument> documents =
        Stream.of(
            new TestDocument("", "aa bb cc"),
            new TestDocument("", "aa bb cc"),
            new TestDocument("", "aa bb cc"),
            new TestDocument("ff . gg . ff . gg"),
            new TestDocument("ff . gg . ff . gg"));

    double[][] expectedPhraseMatrixElements = {{0, 0, 0.577, 0.577, 0.577}};

    check(documents, expectedPhraseMatrixElements);
  }

  @Test
  public void testSinglePhraseWithStopWord() {
    Stream<TestDocument> documents =
        createDocumentsWithTitles("aa stop cc", "aa stop cc", "aa stop cc");

    double[][] expectedPhraseMatrixElements = {{0.707, 0.707}};

    check(documents, expectedPhraseMatrixElements);
  }

  private void check(
      Stream<? extends Document> documents, double[][] expectedPhraseMatrixElements) {
    buildTermDocumentMatrix(documents);
    matrixBuilder.buildTermPhraseMatrix(vsmContext);
    final DoubleMatrix2D phraseMatrix = vsmContext.termPhraseMatrix;

    if (expectedPhraseMatrixElements == null) {
      Assertions.assertThat(phraseMatrix).isNull();
    } else {
      MatrixAssertions.assertThat(phraseMatrix).isEquivalentTo(expectedPhraseMatrixElements, 0.01);
    }
  }
}
