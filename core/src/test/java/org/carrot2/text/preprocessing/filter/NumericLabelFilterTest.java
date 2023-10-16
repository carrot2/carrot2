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

/** Test cases for {@link NumericLabelFilter}. */
public class NumericLabelFilterTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.numericLabelFilter = new NumericLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNonNumericWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("aa . aa", "bb.com.pl . bb.com.pl"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa", "bb.com.pl");
  }

  @Test
  public void testNumericWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("10,12 . 10,12", "bb . bb"));
    Assertions.assertThat(a.labelImages()).containsOnly("bb");
  }

  @Test
  public void testPhraseStartingWithNumbers() {
    PreprocessingContextAssert a = preprocess(new TestDocument("5 xx", "5 xx"));
    Assertions.assertThat(a.labelImages()).containsOnly("xx");
  }

  @Test
  public void testPhraseStartingWithNonNumbers() {
    PreprocessingContextAssert a = preprocess(new TestDocument("xx 5", "xx 5"));
    Assertions.assertThat(a.labelImages()).containsOnly("xx 5", "xx");
  }
}
