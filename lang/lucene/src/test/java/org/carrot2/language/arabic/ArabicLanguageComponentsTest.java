/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.arabic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.assertj.core.api.Assertions;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

public class ArabicLanguageComponentsTest {
  protected LanguageComponents getComponents() {
    return LanguageComponents.load(ArabicLanguageComponents.NAME);
  }

  protected String[][] getStemmingTestData() {
    return new String[][] {
      {"الحسن", "حسن"},
      {"والحسن", "حسن"},
      {"بالحسن", "حسن"},
      {"كالحسن", "حسن"},
      {"فالحسن", "حسن"},
      {"للاخر", "اخر"},
      {"وحسن", "حسن"},
      {"زوجها", "زوج"},
      {"ساهدان", "ساهد"},
      {"ساهدات", "ساهد"},
      {"ساهدون", "ساهد"},
      {"ساهدين", "ساهد"},
      {"ساهديه", "ساهد"},
      {"ساهدية", "ساهد"},
      {"ساهده", "ساهد"},
      {"ساهدة", "ساهد"},
      {"ساهدي", "ساهد"},
      {"وساهدون", "ساهد"},
      {"ساهدهات", "ساهد"},
    };
  }

  protected String[] getCommonWordsTestData() {
    return new String[] {"باستثناء", "اكثر"};
  }

  /** */
  @Test
  public void testStemmerAvailable() {
    assertNotNull(getComponents().get(Stemmer.class));
  }

  /** */
  @Test
  public void testStemming() {
    final Stemmer stemmer = getComponents().get(Stemmer.class);

    for (String[] pair : getStemmingTestData()) {
      Assertions.assertThat(stemmer.stem(pair[0]).toString()).isEqualTo(pair[1]);
    }
  }

  /** */
  @Test
  public void testCommonWords() {
    LexicalData lexicalData = getComponents().get(LexicalData.class);
    final String[] testData = getCommonWordsTestData();
    for (String word : testData) {
      assertTrue(lexicalData.ignoreWord(new MutableCharArray(word)));
    }
  }
}
