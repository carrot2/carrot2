package org.carrot2.language;

import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;

abstract class AbstractLanguageComponentsFactory implements LanguageComponentsFactory {
  // Immutable and thread-safe once loaded, so reuse.
  private final LexicalDataImpl lexicalData;
  private final String langName;
  private final Supplier<Stemmer> stemmerSupplier;

  protected AbstractLanguageComponentsFactory(ResourceLookup resourceLoader, String langName,
                                              Supplier<Stemmer> stemmerSupplier) throws IOException {
    String langPrefix = langName.toLowerCase(Locale.ROOT);
    lexicalData = new LexicalDataImpl(resourceLoader,
        langPrefix + ".stopwords.utf8",
        langPrefix + ".stoplabels.utf8", true);
    this.langName = langName;
    this.stemmerSupplier = stemmerSupplier;
  }

  @Override
  public String name() {
    return langName;
  }

  @Override
  public Stemmer createStemmer() {
    return stemmerSupplier.get();
  }

  @Override
  public Tokenizer createTokenizer() {
    return new ExtendedWhitespaceTokenizer();
  }

  @Override
  public LexicalData createLexicalResources() {
    return lexicalData;
  }
}
