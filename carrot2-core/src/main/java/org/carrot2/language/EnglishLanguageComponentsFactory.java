package org.carrot2.language;

import org.carrot2.language.snowball.EnglishStemmer;
import org.carrot2.util.ClassRelativeResourceLoader;
import org.carrot2.util.ResourceLookup;

import java.io.IOException;

/**
 *
 */
public class EnglishLanguageComponentsFactory implements LanguageComponentsFactory {
  public static final String NAME = "English";

  // Immutable and thread-safe once loaded, so reuse.
  private final LexicalDataImpl lexicalData;

  public EnglishLanguageComponentsFactory() throws IOException {
    this(new ClassRelativeResourceLoader(EnglishLanguageComponentsFactory.class));
  }

  public EnglishLanguageComponentsFactory(ResourceLookup resourceLoader) throws IOException {
    lexicalData = new LexicalDataImpl(resourceLoader, "english.stopwords.utf8", "english.stoplabels.utf8", true);
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Stemmer createStemmer() {
    return new SnowballStemmerAdapter(new EnglishStemmer());
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
