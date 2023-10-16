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

/** Test cases for {@link MinLengthLabelFilter}. */
public class MinLengthLabelFilterTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.minLengthLabelFilter = new MinLengthLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testTooShortWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("aa . aa", "b . b"));
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testLongerWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("abc . abc", "abcd . abcd"));
    Assertions.assertThat(a.labelImages()).containsOnly("abc", "abcd");
  }

  @Test
  public void testShortPhrases() {
    PreprocessingContextAssert a = preprocess(new TestDocument("a a . a a", "b b . b b"));
    Assertions.assertThat(a.labelImages()).containsOnly("a a", "b b");
  }
}
