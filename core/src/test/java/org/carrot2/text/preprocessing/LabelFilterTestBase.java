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
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.junit.Before;

/** Test cases for {@link CompleteLabelFilter}. */
public class LabelFilterTestBase extends TestBase {
  /** Filter processor under tests */
  protected LabelFilterProcessor labelFilterProcessor;

  /** Other preprocessing components required for the test */
  private InputTokenizer tokenizer = new InputTokenizer();

  private CaseNormalizer caseNormalizer = new CaseNormalizer();
  private LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();
  private PhraseExtractor phraseExtractor = new PhraseExtractor(1);
  private StopListMarker stopListMarker = new StopListMarker();

  @Before
  public void setUpPreprocessingComponents() {
    // Disable all filters by default. Tests will enable the filters they need.
    labelFilterProcessor = new LabelFilterProcessor();
    labelFilterProcessor.minLengthLabelFilter = null;
    labelFilterProcessor.queryLabelFilter = null;
    labelFilterProcessor.numericLabelFilter = null;
    labelFilterProcessor.stopWordLabelFilter = null;
    labelFilterProcessor.completeLabelFilter = null;

    initializeFilters(labelFilterProcessor);
  }

  protected void initializeFilters(LabelFilterProcessor filterProcessor) {}

  protected void check(Stream<? extends Document> documents, int[] expectedLabelsFeatureIndex) {
    check(documents, expectedLabelsFeatureIndex, -1);
  }

  protected PreprocessingContext check(
      Stream<? extends Document> documents,
      int[] expectedLabelsFeatureIndex,
      int expectedFirstPhraseIndex) {
    LanguageComponents langComponents =
        CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME);
    PreprocessingContext context = runPreprocessing(documents, langComponents);

    assertThat(context.allLabels.featureIndex)
        .as("allLabels.featureIndex")
        .isEqualTo(expectedLabelsFeatureIndex);
    assertThat(context.allLabels.firstPhraseIndex)
        .as("allLabels.firstPhraseIndex")
        .isEqualTo(expectedFirstPhraseIndex);

    return context;
  }

  protected PreprocessingContext runPreprocessing(
      Stream<? extends Document> documents, LanguageComponents langComponents) {
    return runPreprocessing(documents, langComponents, null);
  }

  protected PreprocessingContext runPreprocessing(
      Stream<? extends Document> documents, LanguageComponents langComponents, String query) {
    PreprocessingContext context = new PreprocessingContext(langComponents);
    tokenizer.tokenize(context, documents);
    caseNormalizer.normalize(context, 1);
    languageModelStemmer.stem(context, query);
    phraseExtractor.extractPhrases(context);
    stopListMarker.mark(context);
    labelFilterProcessor.process(context);
    return context;
  }

  protected PreprocessingContextAssert preprocess(TestDocument... docs) {
    return preprocess(
        null, CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME), docs);
  }

  protected PreprocessingContextAssert preprocess(
      String query, LanguageComponents langComponents, TestDocument... docs) {
    PreprocessingContext ctx = runPreprocessing(Stream.of(docs), langComponents, query);
    return PreprocessingContextAssert.assertThat(ctx);
  }
}
