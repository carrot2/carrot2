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

import org.apache.lucene.analysis.hi.HindiNormalizer;
import org.apache.lucene.analysis.hi.HindiStemmer;
import org.apache.lucene.analysis.in.IndicNormalizer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.language.extras.LuceneStemmerAdapter.StemmingFunction;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class HindiLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Hindi";

  public HindiLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(Stemmer.class, () -> new LuceneStemmerAdapter(new HindiStemming()));
  }

  private class HindiStemming implements StemmingFunction {
    final IndicNormalizer indicNormalizer = new IndicNormalizer();
    final HindiNormalizer hindiNormalizer = new HindiNormalizer();
    final HindiStemmer hindiStemmer = new HindiStemmer();

    @Override
    public int apply(char[] word, int len) {
      len = indicNormalizer.normalize(word, len);
      len = hindiNormalizer.normalize(word, len);
      len = hindiStemmer.stem(word, len);
      return len;
    }
  }
}
