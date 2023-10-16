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
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.junit.Before;
import org.junit.Test;

/** Test cases for {@link SuffixSorter}. */
public class SuffixSorterTest extends TestBase {
  /** Suffix sorter under tests */
  private SuffixSorter suffixSorter;

  /** Other preprocessing components required for the test */
  private InputTokenizer tokenizer;

  private CaseNormalizer caseNormalizer;

  @Before
  public void setUpPreprocessingComponents() {
    tokenizer = new InputTokenizer();
    caseNormalizer = new CaseNormalizer();
    suffixSorter = new SuffixSorter();
  }

  @Test
  public void testEmpty() {
    // Do not add any documents to the rawDocuments list
    int[] expectedSuffixOrder = {0};

    int[] expectedLcpArray = {0};

    checkAsserts(Stream.empty(), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testEmptySnippet() {
    int[] expectedSuffixOrder = {0};

    int[] expectedLcpArray = {0};

    checkAsserts(Stream.of(new TestDocument(null)), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testEmptyBody() {
    int[] expectedSuffixOrder = {0, 1};

    int[] expectedLcpArray = {0, 0};

    checkAsserts(Stream.of(new TestDocument("a")), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testEmptyTitle() {
    int[] expectedSuffixOrder = {0, 1};

    int[] expectedLcpArray = {0, 0};

    checkAsserts(Stream.of(new TestDocument(null, "a")), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testOnePhrase() {
    int[] expectedSuffixOrder = {1, 4, 0, 3, 2, 5};

    int[] expectedLcpArray = {0, 1, 0, 2, 0, 0};

    checkAsserts(Stream.of(new TestDocument("a b", "a b")), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testPunctuation() {
    int[] expectedSuffixOrder = {2, 6, 0, 4, 1, 3, 5, 7};

    int[] expectedLcpArray = {0, 1, 0, 1, 0, 0, 0, 0};

    checkAsserts(
        Stream.of(new TestDocument("a . b", "a . b")), expectedSuffixOrder, expectedLcpArray);
  }

  @Test
  public void testMoreTokens() {
    int[] expectedSuffixOrder = {17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 18};

    int[] expectedLcpArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    checkAsserts(
        Stream.of(new TestDocument("a b c d e  f g h i j  k l m n o  p q r", null)),
        expectedSuffixOrder,
        expectedLcpArray);
  }

  private void checkAsserts(
      Stream<? extends Document> documents, int[] expectedSuffixOrder, int[] expectedLcpArray) {
    PreprocessingContext context =
        new PreprocessingContext(
            CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME));

    tokenizer.tokenize(context, documents);
    caseNormalizer.normalize(context, 1);
    suffixSorter.suffixSort(context);

    Assertions.assertThat(context.allTokens.suffixOrder)
        .as("allTokens.suffixOrder")
        .containsExactly(expectedSuffixOrder);
    Assertions.assertThat(context.allTokens.lcp)
        .as("allTokens.lcp")
        .containsExactly(expectedLcpArray);
  }
}
