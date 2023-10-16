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
package org.carrot2.language.polish;

import java.util.List;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class PolishLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Polish";

  public PolishLanguageComponents() {
    super("Carrot2 (" + NAME + " via Morfologik project)", NAME);
    registerDefaultLexicalData();
    registerResourceless(Stemmer.class, this::createStemmer);
    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
  }

  private Stemmer createStemmer() {
    PolishStemmer stemmer = new PolishStemmer();
    return (word) -> {
      final List<WordData> stems = stemmer.lookup(word);
      if (stems == null || stems.isEmpty()) {
        return null;
      } else {
        return stems.get(0).getStem().toString();
      }
    };
  }
}
