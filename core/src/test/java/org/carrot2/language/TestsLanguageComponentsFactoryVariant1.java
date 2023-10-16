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

public class TestsLanguageComponentsFactoryVariant1 extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "_tests_language_";

  public TestsLanguageComponentsFactoryVariant1() {
    super("test provider: " + NAME, NAME);

    registerResourceless(Stemmer.class, () -> (word) -> null);
    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(StopwordFilter.class, () -> (word) -> true);
    registerResourceless(LabelFilter.class, () -> (label) -> true);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
  }

  @Override
  public String name() {
    return getClass().getName();
  }
}
