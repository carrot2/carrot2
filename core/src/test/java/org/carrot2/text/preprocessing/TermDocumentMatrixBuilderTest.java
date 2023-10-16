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

import com.carrotsearch.hppc.IntIntHashMap;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.attrs.AttrAccess;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.math.matrix.MatrixAssertions;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.junit.Test;

/** Test cases for {@link TermDocumentMatrixBuilder}. */
public class TermDocumentMatrixBuilderTest extends TermDocumentMatrixBuilderTestBase {
  @Test
  public void testEmpty() {
    int[] expectedTdMatrixStemIndices = {};
    double[][] expectedTdMatrixElements = {};

    check(Stream.empty(), expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testSingleWords() {
    Stream<TestDocument> documents =
        Stream.of("aa . bb", "bb . cc", "aa . cc . cc").map(v -> new TestDocument("", v));

    int[] expectedTdMatrixStemIndices = {2, 0, 1};

    double[][] expectedTdMatrixElements = {
      {0, 1, 2},
      {1, 0, 1},
      {1, 1, 0}
    };

    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testSinglePhrase() {
    Stream<TestDocument> documents =
        Stream.of("aa bb cc", "aa bb cc", "aa bb cc").map(v -> new TestDocument("", v));

    int[] expectedTdMatrixStemIndices = {0, 1, 2};
    double[][] expectedTdMatrixElements = {
      {1, 1, 1},
      {1, 1, 1},
      {1, 1, 1},
    };

    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testSinglePhraseWithSingleWords() {
    Stream<TestDocument> documents =
        Stream.of("aa bb cc", "aa bb cc", "aa bb cc", "ff . gg . ff . gg")
            .map(v -> new TestDocument("", v));

    preprocessingPipeline.documentAssigner.minClusterSize.set(1);

    int[] expectedTdMatrixStemIndices = {0, 1, 2, 3, 4};

    double[][] expectedTdMatrixElements = {
      {1, 1, 1, 0},
      {1, 1, 1, 0},
      {1, 1, 1, 0},
      {0, 0, 0, 2},
      {0, 0, 0, 2},
    };

    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testSinglePhraseWithStopWord() {
    Stream<TestDocument> documents =
        Stream.of("aa stop cc", "aa stop cc", "aa stop cc").map(v -> new TestDocument("", v));

    int[] expectedTdMatrixStemIndices = {0, 1};
    double[][] expectedTdMatrixElements = {
      {1, 1, 1},
      {1, 1, 1}
    };

    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testMatrixSizeLimit() {
    Stream<TestDocument> documents =
        Stream.of("aa . aa", "bb . bb . bb", "cc . cc . cc . cc").map(v -> new TestDocument("", v));

    preprocessingPipeline.documentAssigner.minClusterSize.set(1);

    int[] expectedTdMatrixStemIndices = {2, 1};
    double[][] expectedTdMatrixElements = {
      {0, 0, 4},
      {0, 3, 0}
    };

    // Skip preconditions.
    AttrAccess.forceSet(matrixBuilder.maximumMatrixSize, 3 * 2);
    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testTitleWordBoost() {
    Stream<TestDocument> documents =
        Stream.of(
            new TestDocument("aa", "bb"),
            new TestDocument("", "bb . cc"),
            new TestDocument("", "aa . cc . cc"));

    int[] expectedTdMatrixStemIndices = {0, 2, 1};
    double[][] expectedTdMatrixElements = {
      {2, 0, 2},
      {0, 1, 2},
      {1, 1, 0}
    };

    check(documents, expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  @Test
  public void testCarrot905() {
    Stream<TestDocument> documents =
        Stream.of(
            new TestDocument("", "aa . bb"),
            new TestDocument("", "bb . cc"),
            new TestDocument("", "aa . cc . cc"));

    PreprocessingContext context =
        preprocessingPipeline.preprocess(
            documents,
            null,
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME));

    // The preprocessing pipeline will produce increasing indices in tfByDocument,
    // so to reproduce the bug, we need to perturb them, e.g. reverse.
    final int[][] tfByDocument = context.allStems.tfByDocument;
    for (int s = 0; s < tfByDocument.length; s++) {
      final int[] stemTfByDocument = tfByDocument[s];
      for (int i = 0; i < stemTfByDocument.length / 4; i++) {
        int t = stemTfByDocument[i * 2];
        stemTfByDocument[i * 2] = stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2];
        stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2] = t;

        t = stemTfByDocument[i * 2 + 1];
        stemTfByDocument[i * 2 + 1] =
            stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2 + 1];
        stemTfByDocument[(stemTfByDocument.length / 2 - i - 1) * 2 + 1] = t;
      }
    }

    vsmContext = new VectorSpaceModelContext(context);
    matrixBuilder.buildTermDocumentMatrix(vsmContext);
    matrixBuilder.buildTermPhraseMatrix(vsmContext);

    int[] expectedTdMatrixStemIndices = {2, 0, 1};
    double[][] expectedTdMatrixElements = {
      {0, 1, 2},
      {1, 0, 1},
      {1, 1, 0}
    };

    checkOnly(expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  private void check(
      Stream<? extends Document> documents,
      double[][] expectedTdMatrixElements,
      int[] expectedTdMatrixStemIndices) {
    buildTermDocumentMatrix(documents);
    checkOnly(expectedTdMatrixElements, expectedTdMatrixStemIndices);
  }

  private void checkOnly(double[][] expectedTdMatrixElements, int[] expectedTdMatrixStemIndices) {
    Assertions.assertThat(vsmContext.termDocumentMatrix.rows())
        .as("tdMatrix.rowCount")
        .isEqualTo(expectedTdMatrixStemIndices.length);
    MatrixAssertions.assertThat(vsmContext.termDocumentMatrix)
        .isEquivalentTo(expectedTdMatrixElements);

    final IntIntHashMap expectedStemToRowIndex = new IntIntHashMap();
    for (int i = 0; i < expectedTdMatrixStemIndices.length; i++) {
      expectedStemToRowIndex.put(expectedTdMatrixStemIndices[i], i);
    }

    Assertions.assertThat((Object) new IntIntHashMap(vsmContext.stemToRowIndex))
        .isEqualTo(expectedStemToRowIndex);
  }
}
