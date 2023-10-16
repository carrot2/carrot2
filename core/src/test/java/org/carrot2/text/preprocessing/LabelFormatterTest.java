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

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.junit.Test;

/** Test cases for {@link LabelFormatter}. */
public class LabelFormatterTest extends TestBase {
  /** Other preprocessing components required for the test */
  private InputTokenizer tokenizer = new InputTokenizer();

  private CaseNormalizer caseNormalizer = new CaseNormalizer();
  private LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();
  private PhraseExtractor phraseExtractor = new PhraseExtractor(1);
  private StopListMarker stopListMarker = new StopListMarker();
  private LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

  private LanguageComponents langComponents = CachedLangComponents.loadCached("English");

  @Test
  public void testSingleWordNotCapitalized() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("test", "test"));
    final String expectedLabel = "Test";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"test".toCharArray()}, new boolean[] {false}, expectedLabel, true);
  }

  @Test
  public void testSingleWordCapitalized() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("kMN", "kMN"));
    final String expectedLabel = "kMN";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"kMN".toCharArray()}, new boolean[] {false}, expectedLabel, true);
  }

  @Test
  public void testSingleStopWord() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("for", "for"));
    labelFilterProcessor.stopWordLabelFilter = null;
    final String expectedLabel = "For";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"for".toCharArray()}, new boolean[] {true}, expectedLabel, true);
  }

  @Test
  public void testPhraseWithLowerCaseWords() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("test phrase", "test phrase"));
    final String expectedLabel = "Test Phrase";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"test".toCharArray(), "phrase".toCharArray()},
        new boolean[] {false, false},
        expectedLabel,
        true);
  }

  @Test
  public void testPhraseWithStopWords() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("food for fish", "food for fish"));
    final String expectedLabel = "Food for Fish";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"food".toCharArray(), "for".toCharArray(), "fish".toCharArray()},
        new boolean[] {false, true, false},
        expectedLabel,
        true);
  }

  @Test
  public void testPhraseWithoutStopWords() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("Jaguar car", "Jaguar car"));
    final String expectedLabel = "Jaguar Car";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"Jaguar".toCharArray(), "Car".toCharArray()},
        new boolean[] {false, true, false},
        expectedLabel,
        true);
  }

  @Test
  public void testPhraseWithCapitalizedWords() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("iMac stuff", "iMac stuff"));
    final String expectedLabel = "iMac Stuff";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"iMac".toCharArray(), "stuff".toCharArray()},
        new boolean[] {false, false},
        expectedLabel,
        true);
  }

  @Test
  public void testChinesePhrases() {
    Stream<TestDocument> documents = Stream.of(new TestDocument("东亚货币贬值", "东亚货币贬值"));
    final String expectedLabel = "东亚货币贬值";

    checkFullPreprocessing(documents, langComponents, expectedLabel);
    checkWithoutPreprocessing(
        new char[][] {"东亚货币贬值".toCharArray()}, new boolean[] {false, false}, expectedLabel, false);
  }

  private void checkWithoutPreprocessing(
      char[][] words, boolean[] stopWords, String expectedFormattedLabel, boolean joinWithSpace) {
    LabelFormatter labelFormatter = new LabelFormatterImpl(joinWithSpace ? " " : "");
    Assertions.assertThat(labelFormatter.format(words, stopWords))
        .isEqualTo(expectedFormattedLabel);
  }

  private void checkFullPreprocessing(
      Stream<? extends Document> documents,
      LanguageComponents langComponents,
      String... expectedFormattedLabels) {
    PreprocessingContext context = new PreprocessingContext(langComponents);
    tokenizer.tokenize(context, documents);
    caseNormalizer.normalize(context, 1);
    languageModelStemmer.stem(context, null);
    phraseExtractor.extractPhrases(context);
    stopListMarker.mark(context);
    labelFilterProcessor.process(context);

    LabelFormatter labelFormatter = context.languageComponents.get(LabelFormatter.class);
    final int[] labelsFeatureIndex = context.allLabels.featureIndex;
    Assertions.assertThat(
            IntStream.of(labelsFeatureIndex)
                .mapToObj(feature -> context.format(labelFormatter, feature)))
        .containsExactly(expectedFormattedLabels);
  }
}
