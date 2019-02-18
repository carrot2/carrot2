package org.carrot2.language;

public class TestsLanguageComponentsFactoryVariant1 implements LanguageComponentsFactory {
  public static final String NAME = "_tests_language_";

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Stemmer createStemmer() {
    return (word) -> null;
  }

  @Override
  public Tokenizer createTokenizer() {
    return new ExtendedWhitespaceTokenizer();
  }

  @Override
  public LexicalData createLexicalResources() {
    return new LexicalData() {};
  }
}
