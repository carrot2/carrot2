package org.carrot2.language.polish;

import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.carrot2.language.*;
import org.carrot2.util.ClassRelativeResourceLoader;
import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class PolishLanguageComponentsFactory implements LanguageComponentsFactory {
  public static final String NAME = "Polish";

  // Immutable and thread-safe once loaded, so reuse.
  private final LexicalDataImpl lexicalData;

  public PolishLanguageComponentsFactory() throws IOException {
    this(new ClassRelativeResourceLoader(PolishLanguageComponentsFactory.class));
  }

  public PolishLanguageComponentsFactory(ResourceLookup resourceLoader) throws IOException {
    lexicalData = new LexicalDataImpl(resourceLoader, "polish.stopwords.utf8", "polish.stoplabels.utf8", true);
  }

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public Stemmer createStemmer() {
    PolishStemmer stemmer = new PolishStemmer();
    return (word) ->{
      final List<WordData> stems = stemmer.lookup(word);
      if (stems == null || stems.isEmpty()) {
        return null;
      } else {
        return stems.get(0).getStem().toString();
      }
    };
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
