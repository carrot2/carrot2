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
package org.carrot2.language.japanese;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.Stemmer;
import org.carrot2.language.StopwordFilter;
import org.carrot2.language.Tokenizer;
import org.carrot2.util.MutableCharArray;
import org.junit.Test;

public abstract class AbstractLanguageComponentsTest {
  protected final LanguageComponents components;
  private final String[][] stemmingPairs;
  private final String[] stopWords;

  AbstractLanguageComponentsTest(String language, String[] stopWords, String[][] stemmingPairs)
      throws IOException {
    this.components = LanguageComponents.loader().load().language(language);
    this.stemmingPairs = stemmingPairs;
    this.stopWords = stopWords;
  }

  /** */
  @Test
  public void testStemmerAvailable() {
    assertNotNull(components.get(Stemmer.class));
  }

  /** */
  @Test
  public void testStemming() {
    final Stemmer stemmer = components.get(Stemmer.class);
    for (String[] pair : stemmingPairs) {
      CharSequence stem = stemmer.stem(pair[0]);
      Assertions.assertThat(stem == null ? null : stem.toString()).isEqualTo(pair[1]);
    }
  }

  /** */
  @Test
  public void testCommonWords() {
    StopwordFilter wordFilter = components.get(StopwordFilter.class);
    for (String word : stopWords) {
      Assertions.assertThat(wordFilter.test(new MutableCharArray(word))).as(word).isFalse();
    }
  }

  protected List<String> tokenize(Tokenizer tokenizer, String input) throws IOException {
    tokenizer.reset(new StringReader(input));
    MutableCharArray buffer = new MutableCharArray();
    ArrayList<String> tokens = new ArrayList<>();
    while (tokenizer.nextToken() >= 0) {
      tokenizer.setTermBuffer(buffer);
      tokens.add(buffer.toString());
    }
    return tokens;
  }
}
