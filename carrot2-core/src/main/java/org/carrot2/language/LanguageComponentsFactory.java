package org.carrot2.language;

public interface LanguageComponentsFactory {
  String name();
  Stemmer createStemmer();
  Tokenizer createTokenizer();
  LexicalData createLexicalResources();
}
