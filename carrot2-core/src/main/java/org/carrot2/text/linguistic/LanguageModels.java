package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;

public class LanguageModels {
  public static LanguageModel english() {
    return new LanguageModel(
        new DefaultStemmerFactory().getStemmer(LanguageCode.ENGLISH),
        new DefaultTokenizerFactory().getTokenizer(LanguageCode.ENGLISH),
        new DefaultLexicalDataFactory().getLexicalData(LanguageCode.ENGLISH));
  }
}
