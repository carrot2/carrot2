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
import org.carrot2.clustering.TestDocument;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.carrot2.text.preprocessing.PreprocessingContextAssert;
import org.junit.Test;

/** Test cases for {@link StopWordLabelFilter}. */
public class CompleteLabelFilterTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.stopWordLabelFilter = new StopWordLabelFilter();
    filterProcessor.completeLabelFilter = new CompleteLabelFilter();
    filterProcessor.completeLabelFilter.labelOverrideThreshold.set(0.5);
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testOnePhrase() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("aa bb cc . aa bb cc", "aa bb cc . aa bb cc"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb cc");
  }

  @Test
  public void testSubphrases() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("aa bb cc . aa bb cc", "bb cc . bb cc"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb cc");
  }

  @Test
  public void testNestedPhrases() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("aa bb cc dd . aa bb cc dd", "aa bb dd . aa bb dd"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb cc dd", "aa bb dd");
  }

  @Test
  public void testFuzzyOverrideApplied() {
    labelFilterProcessor.completeLabelFilter.labelOverrideThreshold.set(0.3);
    PreprocessingContextAssert a =
        preprocess(
            new TestDocument(
                "aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb cc dd");
  }

  @Test
  public void testFuzzyOverrideNotApplied() {
    PreprocessingContextAssert a =
        preprocess(
            new TestDocument(
                "aa bb cc . aa bb cc . aa bb cc . aa bb cc . aa bb cc dd . aa bb cc dd"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb cc dd", "aa bb cc");
  }

  @Test
  public void testOverridingByFilteredOutPhrase() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("stop aa bb stop . stop aa bb stop"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa bb");
  }
}
