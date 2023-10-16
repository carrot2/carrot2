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

import org.apache.lucene.analysis.gl.GalicianStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneStemmerAdapter.StemmingFunction;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class GalicianLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Galician";

  public GalicianLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(Stemmer.class, () -> new LuceneStemmerAdapter(new GalicianStemming(), 1));
  }

  private class GalicianStemming implements StemmingFunction {
    final GalicianStemmer stemmer = new GalicianStemmer();

    @Override
    public int apply(char[] word, int len) {
      return stemmer.stem(word, len);
    }
  }
}
