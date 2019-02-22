package org.carrot2.language;

import org.carrot2.util.MutableCharArray;

public class TestsLanguageComponentsFactoryVariant2 implements LanguageComponentsFactory {
  public static final String NAME = "_tests_language_variant2_";

  private static final class LexicalDataImpl implements LexicalData {
    @Override
    public boolean ignoreWord(CharSequence word) {
      return word.toString().contains("stop");
    }

    @Override
    public boolean ignoreLabel(CharSequence formattedLabel) {
      return formattedLabel.toString().startsWith("stoplabel");
    }

    @Override
    public boolean usesSpaceDelimiters() {
      return true;
    }
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Stemmer createStemmer() {
    return (word) -> {
      if (word.length() > 2) {
        return word.subSequence(0, word.length() - 2);
      } else {
        return null;
      }
    };
  }

  @Override
  public Tokenizer createTokenizer() {
    return new ExtendedWhitespaceTokenizer();
  }

  @Override
  public LexicalData createLexicalResources() {
    return new LexicalDataImpl();
  }
}
