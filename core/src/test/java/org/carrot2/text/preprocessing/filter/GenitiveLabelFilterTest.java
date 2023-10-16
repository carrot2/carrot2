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

/** Test cases for {@link GenitiveLabelFilter}. */
public class GenitiveLabelFilterTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.genitiveLabelFilter = new GenitiveLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNoGenitiveWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("abc . abc", "abcd . abcd"));
    Assertions.assertThat(a.labelImages()).containsOnly("abc", "abcd");
  }

  @Test
  public void testGenitiveWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("abcs' . abcs'", "abcd`s . abcd`s"));
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNoGenitiveEndingPhrases() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("country's minister'll . country's minister'll"));
    Assertions.assertThat(a.labelImages()).noneMatch(label -> label.endsWith("'s"));
  }

  @Test
  public void testGenitiveEndingPhrases() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("country minister`s . country's minister`s"));
    Assertions.assertThat(a.labelImages()).containsOnly("country");
  }
}
