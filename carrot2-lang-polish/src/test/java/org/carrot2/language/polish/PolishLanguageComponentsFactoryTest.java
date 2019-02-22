package org.carrot2.language.polish;

import org.assertj.core.api.Assertions;
import org.carrot2.language.EnglishLanguageComponentsFactory;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

import static org.junit.Assert.*;

public class PolishLanguageComponentsFactoryTest {
  protected LanguageComponents getComponents() {
    return LanguageComponents.get(PolishLanguageComponentsFactory.NAME);
  }

  protected String[][] getStemmingTestData() {
    return new String[][] {
        { "okropnymi", "okropny" },
        { "owocami", "owoc" }
    };
  }

  protected String[] getCommonWordsTestData() {
    return new String[] {
        "aby", "albo", "bez", "i"
    };
  }

  /**
   *
   */
  @Test
  public void testStemmerAvailable() {
    assertNotNull(getComponents().stemmer);
  }

  /**
   *
   */
  @Test
  public void testStemming() {
    final Stemmer stemmer = getComponents().stemmer;

    for (String[] pair : getStemmingTestData()) {
      Assertions.assertThat(stemmer.stem(pair[0]).toString())
          .isEqualTo(pair[1]);
    }
  }

  /**
   *
   */
  @Test
  public void testCommonWords() {
    LexicalData lexicalData = getComponents().lexicalData;
    final String[] testData = getCommonWordsTestData();
    for (String word : testData) {
      assertTrue(lexicalData.ignoreWord(new MutableCharArray(word)));
    }
  }
}
