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

import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.junit.Test;

public class LanguageComponentsTest extends TestBase {
  @Test
  public void testDefaultLanguageModels() throws IOException {
    Assertions.assertThat(LanguageComponents.loader().load().languages())
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
            "Turkish");

    for (String lang : LanguageComponents.loader().load().languages()) {
      LanguageComponents actual = CachedLangComponents.loadCached(lang);
      Assertions.assertThat(actual.get(Tokenizer.class)).as("Tokenizer for " + lang).isNotNull();
      Assertions.assertThat(actual.get(Stemmer.class)).as("Stemmer for " + lang).isNotNull();
      Assertions.assertThat(actual.get(StopwordFilter.class))
          .as("Word filter for " + lang)
          .isNotNull();
      Assertions.assertThat(actual.get(LabelFilter.class))
          .as("Word filter for " + lang)
          .isNotNull();
    }
  }

  @Test
  public void testCustomComponentInjection() {
    LanguageComponents english = CachedLangComponents.loadCached("English");
    Assertions.assertThat(english.components()).contains(Runnable.class);
    english.get(Runnable.class).run();
  }
}
