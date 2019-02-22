
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.language;

import org.carrot2.TestBase;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Superclass for testing language components.
 */
public abstract class LanguageComponentsFactoryTestBase extends TestBase {
  /**
   * @return Returns language code for this test.
   */
  protected abstract LanguageComponents getComponents();

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
    final String[][] testData = getStemmingTestData();
    final Stemmer stemmer = getComponents().stemmer;

    for (String[] pair : testData) {
      CharSequence stemmed = stemmer.stem(pair[0]);
      assertEquals("Stemming difference: " + pair[0] + " should become " + pair[1]
          + " but was transformed into " + stemmed, pair[1], stemmed == null ? null
          : stemmed.toString());
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

  /**
   * Override and provide word pairs for the stemmer.
   * Sample data should follow this format:
   *
   * <pre>
   * return new String [] []
   * {
   *     {
   *         &quot;inflected&quot;, &quot;base&quot;
   *     },
   *     {
   *         &quot;inflected&quot;, &quot;base&quot;
   *     },
   * };
   * </pre>
   */
  protected String[][] getStemmingTestData() {
    return new String[][] {
        /* Empty by default. */
    };
  }

  /**
   * Override and provide words for testing common word flags.
   */
  protected String[] getCommonWordsTestData() {
    return new String[] {
        /* Empty by default. */
    };
  }

}
