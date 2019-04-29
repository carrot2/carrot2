package org.carrot2.language.polish;

import org.assertj.core.api.Assertions;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PolishLanguageComponentsTest {
  protected LanguageComponents getComponents() throws IOException {
    return LanguageComponents.load(PolishLanguageComponents.NAME);
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
  public void testStemmerAvailable() throws Exception {
    assertNotNull(getComponents().get(Stemmer.class));
  }

  /**
   *
   */
  @Test
  public void testStemming() throws Exception {
    final Stemmer stemmer = getComponents().get(Stemmer.class);

    for (String[] pair : getStemmingTestData()) {
      Assertions.assertThat(stemmer.stem(pair[0]).toString())
          .isEqualTo(pair[1]);
    }
  }

  /**
   *
   */
  @Test
  public void testCommonWords() throws Exception {
    LexicalData lexicalData = getComponents().get(LexicalData.class);
    final String[] testData = getCommonWordsTestData();
    for (String word : testData) {
      assertTrue(lexicalData.ignoreWord(new MutableCharArray(word)));
    }
  }
}
