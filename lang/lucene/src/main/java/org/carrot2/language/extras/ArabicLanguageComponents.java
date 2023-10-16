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

import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class ArabicLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Arabic";

  public ArabicLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(
        Stemmer.class,
        () -> {
          final ArabicStemmer stemmer = new ArabicStemmer();
          final ArabicNormalizer normalizer = new ArabicNormalizer();
          return new LuceneStemmerAdapter(
              (word, len) -> {
                int newLen = normalizer.normalize(word, len);
                newLen = stemmer.stem(word, newLen);
                return newLen;
              });
        });
  }
}
