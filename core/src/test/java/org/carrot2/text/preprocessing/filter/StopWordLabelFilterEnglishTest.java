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
package org.carrot2.text.preprocessing.filter;

import org.assertj.core.api.Assertions;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.clustering.TestDocument;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.carrot2.text.preprocessing.PreprocessingContextAssert;
import org.junit.Test;

/** Test cases for {@link StopLabelFilter}. */
public class StopWordLabelFilterEnglishTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.completeLabelFilter = new CompleteLabelFilter();
    filterProcessor.stopWordLabelFilter = new StopWordLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNonStopWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("coal . mining", "coal . mining"));
    Assertions.assertThat(a.labelImages()).containsOnly("coal", "mining");
  }

  @Test
  public void testStopWords() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("I . HAVE . coal", "I . HAVE . coal"));
    Assertions.assertThat(a.labelImages()).containsOnly("coal");
  }

  @Test
  public void testStopWordsInPhrases() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("of coal mining for", "of coal mining for"));
    Assertions.assertThat(a.labelImages()).containsOnly("coal mining");
  }

  @Override
  protected PreprocessingContextAssert preprocess(TestDocument... docs) {
    return super.preprocess(null, CachedLangComponents.loadCached("English"), docs);
  }
}
