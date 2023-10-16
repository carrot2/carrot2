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
package org.carrot2.language.extras;

import org.apache.lucene.analysis.id.IndonesianStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneStemmerAdapter.StemmingFunction;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class IndonesianLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Indonesian";

  public IndonesianLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(Stemmer.class, () -> new LuceneStemmerAdapter(new IndonesianStemming()));
  }

  private class IndonesianStemming implements StemmingFunction {
    private final IndonesianStemmer stemmer = new IndonesianStemmer();

    @Override
    public int apply(char[] buffer, int length) {
      return stemmer.stem(buffer, length, true);
    }
  }
}
