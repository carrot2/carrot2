package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;

public class LanguageModels {
  private static DefaultStemmerFactory stemmerFactory = new DefaultStemmerFactory();
  private static DefaultTokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
  private static DefaultLexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

  public static LanguageModel english() {
    return new LanguageModel(
        stemmerFactory.getStemmer(LanguageCode.ENGLISH),
        tokenizerFactory.getTokenizer(LanguageCode.ENGLISH),
        lexicalDataFactory.getLexicalData(LanguageCode.ENGLISH));
  }
}
