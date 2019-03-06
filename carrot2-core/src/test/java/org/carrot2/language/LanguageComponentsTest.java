
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

import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class LanguageComponentsTest extends TestBase {
  @Test
  public void testDefaultLanguageModels() {
    Assertions.assertThat(LanguageComponents.languages())
        .containsOnly(
            TestsLanguageComponentsFactoryVariant1.NAME,
            TestsLanguageComponentsFactoryVariant2.NAME,
            "English",
            "Danish",
            "Dutch",
            "Finnish",
            "French",
            "German",
            "Hungarian",
            "Italian",
            "Norwegian",
            "Portuguese",
            "Romanian",
            "Russian",
            "Spanish",
            "Swedish",
            "Turkish"
        );

    for (String lang : LanguageComponents.languages()) {
      LanguageComponents actual = LanguageComponents.load(lang);
      Assertions.assertThat(actual.get(Tokenizer.class)).as("Tokenizer for " + lang).isNotNull();
      Assertions.assertThat(actual.get(Stemmer.class)).as("Stemmer for " + lang).isNotNull();
      Assertions.assertThat(actual.get(LexicalData.class)).as("Lexical data for " + lang).isNotNull();
    }
  }

  @Test
  public void testCustomComponentInjection() {
    LanguageComponents english = LanguageComponents.load("English");
    Assertions.assertThat(english.components())
        .contains(Runnable.class);
    english.get(Runnable.class).run();
  }
}