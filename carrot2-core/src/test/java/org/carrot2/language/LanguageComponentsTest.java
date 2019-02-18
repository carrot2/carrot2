
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
import org.carrot2.AbstractTest;
import org.junit.Test;

public class LanguageComponentsTest extends AbstractTest {
  @Test
  public void testDefaultLanguageModels() {
    Assertions.assertThat(LanguageComponents.languages())
        .containsOnly(
            TestsLanguageComponentsFactoryVariant1.NAME,
            TestsLanguageComponentsFactoryVariant2.NAME,
            EnglishLanguageComponentsFactory.NAME
        );

    for (String lang : LanguageComponents.languages()) {
      LanguageComponents actual = LanguageComponents.get(lang);
      Assertions.assertThat(actual.tokenizer).as("Tokenizer for " + lang).isNotNull();
      Assertions.assertThat(actual.stemmer).as("Stemmer for " + lang).isNotNull();
      Assertions.assertThat(actual.lexicalData).as("Lexical data for " + lang).isNotNull();
    }
  }
}