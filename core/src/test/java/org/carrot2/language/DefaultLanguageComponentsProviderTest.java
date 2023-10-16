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
package org.carrot2.language;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.StringReader;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

public class DefaultLanguageComponentsProviderTest extends TestBase {
  @Test
  public void testEnglish() throws IOException {
    check(
        "English",
        new String[][] {
          {"pulps", "pulp"},
          {"driving", "drive"},
          {"king's", "king"},
          {"mining", "mine"}
        },
        new String[] {"and", "or", "to", "from"});
  }

  @Test
  public void testDanish() throws IOException {
    check("Danish", new String[][] {}, new String[] {"enhver"});
  }

  @Test
  public void testDutch() throws IOException {
    check("Dutch", new String[][] {}, new String[] {"daaruit"});
  }

  @Test
  public void testFinnish() throws IOException {
    check("Finnish", new String[][] {}, new String[] {});
  }

  @Test
  public void testFrench() throws IOException {
    check("French", new String[][] {}, new String[] {});
  }

  @Test
  public void testGerman() throws IOException {
    check("German", new String[][] {}, new String[] {});
  }

  @Test
  public void testHungarian() throws IOException {
    check("Hungarian", new String[][] {}, new String[] {});
  }

  @Test
  public void testItalian() throws IOException {
    check("Italian", new String[][] {}, new String[] {});
  }

  @Test
  public void testNorwegian() throws IOException {
    check("Norwegian", new String[][] {}, new String[] {});
  }

  @Test
  public void testPortuguese() throws IOException {
    check("Portuguese", new String[][] {}, new String[] {});
  }

  @Test
  public void testRomanian() throws IOException {
    check("Romanian", new String[][] {}, new String[] {});
  }

  @Test
  public void testRussian() throws IOException {
    check("Russian", new String[][] {}, new String[] {"вернуться"});
  }

  @Test
  public void testSpanish() throws IOException {
    check("Spanish", new String[][] {}, new String[] {});
  }

  @Test
  public void testSwedish() throws IOException {
    check("Swedish", new String[][] {}, new String[] {});
  }

  @Test
  public void testTurkish() throws IOException {
    check("Turkish", new String[][] {}, new String[] {"birþeyi"});
  }

  private void check(String language, String[][] stemmingData, String[] commonWords)
      throws IOException {
    LanguageComponents components = CachedLangComponents.loadCached(language);

    Tokenizer tokenizer = components.get(Tokenizer.class);
    tokenizer.reset(new StringReader(""));
    Assertions.assertThat(tokenizer.nextToken()).isEqualTo((short) Tokenizer.TT_EOF);

    final Stemmer stemmer = components.get(Stemmer.class);
    for (String[] pair : stemmingData) {
      Assertions.assertThat(stemmer.stem(pair[0]).toString()).isEqualTo(pair[1]);
    }

    StopwordFilter wordFilter = components.get(StopwordFilter.class);
    for (String word : commonWords) {
      assertFalse(wordFilter.test(new MutableCharArray(word)));
    }
  }
}
