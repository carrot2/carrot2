
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import org.carrot2.AbstractTest;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.TestDocument;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.junit.Before;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test cases for {@link CompleteLabelFilter}.
 */
public class LabelFilterTestBase extends AbstractTest {
  /**
   * Filter processor under tests
   */
  protected LabelFilterProcessor labelFilterProcessor;

  /**
   * Other preprocessing components required for the test
   */
  private InputTokenizer tokenizer = new InputTokenizer();
  private CaseNormalizer caseNormalizer = new CaseNormalizer();
  private LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();
  private PhraseExtractor phraseExtractor = new PhraseExtractor(1);
  private StopListMarker stopListMarker = new StopListMarker();

  @Before
  public void setUpPreprocessingComponents() {
    // Disable all filters by default. Tests will enable the filters they need.
    labelFilterProcessor = new LabelFilterProcessor();
    labelFilterProcessor.minLengthLabelFilter.enabled.set(false);
    labelFilterProcessor.queryLabelFilter.enabled.set(false);
    labelFilterProcessor.numericLabelFilter.enabled.set(false);
    labelFilterProcessor.stopWordLabelFilter.enabled.set(false);
    labelFilterProcessor.completeLabelFilter.enabled.set(false);

    initializeFilters(labelFilterProcessor);
  }

  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
  }

  protected void check(Stream<? extends Document> documents, int[] expectedLabelsFeatureIndex) {
    check(documents, expectedLabelsFeatureIndex, -1);
  }

  protected PreprocessingContext check(Stream<? extends Document> documents, int[] expectedLabelsFeatureIndex, int expectedFirstPhraseIndex) {
    LanguageComponents langComponents = LanguageComponents.get(TestsLanguageComponentsFactoryVariant2.NAME);
    PreprocessingContext context = runPreprocessing(documents, langComponents);

    assertThat(context.allLabels.featureIndex).as("allLabels.featureIndex")
        .isEqualTo(expectedLabelsFeatureIndex);
    assertThat(context.allLabels.firstPhraseIndex).as("allLabels.firstPhraseIndex")
        .isEqualTo(expectedFirstPhraseIndex);

    return context;
  }

  protected PreprocessingContext runPreprocessing(Stream<? extends Document> documents, LanguageComponents langComponents) {
    return runPreprocessing(documents, langComponents, null);
  }

  protected PreprocessingContext runPreprocessing(Stream<? extends Document> documents, LanguageComponents langComponents, String query) {
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
    return preprocess(null, LanguageComponents.get(TestsLanguageComponentsFactoryVariant2.NAME), docs);
  }

  protected PreprocessingContextAssert preprocess(String query, LanguageComponents langComponents, TestDocument... docs) {
    PreprocessingContext ctx = runPreprocessing(Stream.of(docs), langComponents, query);
    return PreprocessingContextAssert.assertThat(ctx);
  }
}
