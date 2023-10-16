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

import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

public class TestsLanguageComponentsFactoryVariant2 extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "_tests_language_variant2_";

  public TestsLanguageComponentsFactoryVariant2() {
    super("test provider: " + NAME, NAME);

    registerResourceless(Stemmer.class, this::createStemmer);
    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    StopwordFilter wordFilter = (word) -> !word.toString().contains("stop");
    registerResourceless(StopwordFilter.class, () -> wordFilter);
    LabelFilter labelFilter = (label) -> !label.toString().startsWith("stoplabel");
    registerResourceless(LabelFilter.class, () -> labelFilter);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
  }

  private Stemmer createStemmer() {
    return (word) -> {
      if (word.length() > 2) {
        return word.subSequence(0, word.length() - 2);
      } else {
        return null;
      }
    };
  }

  @Override
  public String name() {
    return getClass().getName();
  }
}
