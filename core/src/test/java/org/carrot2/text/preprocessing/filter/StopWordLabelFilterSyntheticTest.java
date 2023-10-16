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
public class StopWordLabelFilterSyntheticTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.stopWordLabelFilter = new StopWordLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNonStopWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("aa . aa", "bb . bb"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa", "bb");
  }

  @Test
  public void testStopWords() {
    PreprocessingContextAssert a = preprocess(new TestDocument("stop . stop", "bb . bb"));
    Assertions.assertThat(a.labelImages()).containsOnly("bb");
  }

  @Test
  public void testNonStopPhrases() {
    PreprocessingContextAssert a = preprocess(new TestDocument("aa aa . aa aa", "bb bb . bb bb"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa", "aa aa", "bb", "bb bb");
  }

  @Test
  public void testStopPhrases() {
    PreprocessingContextAssert a =
        preprocess(
            new TestDocument("aa stop aa . aa stop aa", "stop bb . stop bb . bb stop . bb stop"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa", "bb", "aa stop aa");
  }

  @Test
  public void testStopPhrasesWithStemmedWords() {
    PreprocessingContextAssert a =
        preprocess(
            new TestDocument("aa1 . aa2 . aa1 . aa2", "stop aa1 aa2. stop aa1 aa2 . stop aa1 aa2"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa1", "aa1 aa2");
  }

  @Test
  public void testStemmedWords() {
    PreprocessingContextAssert a =
        preprocess(new TestDocument("abc . abc . abc", "abd . abd . abe . abe"));
    Assertions.assertThat(a.labelImages()).containsOnly("abc");
  }
}
