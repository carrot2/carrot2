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

import java.util.Objects;
import org.apache.lucene.analysis.br.BrazilianStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class BrazilianLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Brazilian";

  public BrazilianLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(
        Stemmer.class, () -> new LuceneStemmerAdapter(new BrazilianStemmerAdapter()::stems, 5));
  }

  private class BrazilianStemmerAdapter extends BrazilianStemmer {
    public int stems(char[] chars, int len) {
      String word = new String(chars, 0, len);
      String stem = super.stem(word);

      if (stem == null || Objects.equals(word, stem)) {
        return len;
      } else {
        stem.getChars(0, stem.length(), chars, 0);
        return stem.length();
      }
    }
  }
}
