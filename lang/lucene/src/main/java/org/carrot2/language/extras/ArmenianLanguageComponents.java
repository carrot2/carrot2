/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.extras;

import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.LexicalData;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.tartarus.snowball.ext.ArmenianStemmer;

/** */
public class ArmenianLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Armenian";

  public ArmenianLanguageComponents() {
    super("Carrot2 (extras)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    register(
        LexicalData.class,
        (language, resourceLookup) -> {
          LexicalData lexicalData = loadLexicalData(language, resourceLookup);
          return () -> lexicalData;
        });
    registerResourceless(
        Stemmer.class, () -> new LuceneSnowballStemmerAdapter(new ArmenianStemmer()));
  }
}
