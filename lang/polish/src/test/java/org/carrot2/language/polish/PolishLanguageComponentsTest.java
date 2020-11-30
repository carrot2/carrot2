/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.polish;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.Stemmer;
import org.carrot2.language.WordFilter;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

public class PolishLanguageComponentsTest {
  protected LanguageComponents getComponents() throws IOException {
    return LanguageComponents.loader().load().language(PolishLanguageComponents.NAME);
  }

  protected String[][] getStemmingTestData() {
    return new String[][] {
      {"okropnymi", "okropny"},
      {"owocami", "owoc"}
    };
  }

  protected String[] getCommonWordsTestData() {
    return new String[] {"aby", "albo", "bez", "i"};
  }

  /** */
  @Test
  public void testStemmerAvailable() throws Exception {
    assertNotNull(getComponents().get(Stemmer.class));
  }

  /** */
  @Test
  public void testStemming() throws Exception {
    final Stemmer stemmer = getComponents().get(Stemmer.class);

    for (String[] pair : getStemmingTestData()) {
      Assertions.assertThat(stemmer.stem(pair[0]).toString()).isEqualTo(pair[1]);
    }
  }

  /** */
  @Test
  public void testCommonWords() throws Exception {
    WordFilter wordFilter = getComponents().get(WordFilter.class);
    for (String word : getCommonWordsTestData()) {
      Assertions.assertThat(wordFilter.ignoreWord(new MutableCharArray(word))).as(word).isTrue();
    }
  }
}
