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
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.carrot2.text.preprocessing.PreprocessingContextAssert;
import org.junit.Test;

/** Test cases for {@link QueryLabelFilter}. */
public class QueryLabelFilterTest extends LabelFilterTestBase {
  @Override
  protected void initializeFilters(LabelFilterProcessor filterProcessor) {
    filterProcessor.queryLabelFilter = new QueryLabelFilter();
  }

  @Test
  public void testEmpty() {
    PreprocessingContextAssert a = preprocess();
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testNonQueryWords() {
    PreprocessingContextAssert a = preprocess("Query word", new TestDocument("aa . aa", "bb . bb"));
    Assertions.assertThat(a.labelImages()).containsOnly("aa", "bb");
  }

  @Test
  public void testQueryWords() {
    PreprocessingContextAssert a =
        preprocess("Query word", new TestDocument("query . Word", "query . word"));
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testPhraseWithAllQueryWords() {
    PreprocessingContextAssert a =
        preprocess("Query word", new TestDocument("query query word", "query query word"));
    Assertions.assertThat(a.labelImages()).isEmpty();
  }

  @Test
  public void testPhraseWithSomeQueryWords() {
    PreprocessingContextAssert a =
        preprocess("Query word", new TestDocument("query word test", "query word test"));
    Assertions.assertThat(a.labelImages()).containsOnly("test", "word test", "query word test");
  }

  private PreprocessingContextAssert preprocess(String query, TestDocument... documents) {
    return super.preprocess(
        query,
        CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME),
        documents);
  }
}
