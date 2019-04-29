package org.carrot2.language.polish;

import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.carrot2.language.*;
import org.carrot2.util.ClassRelativeResourceLoader;
import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

/**
 *
 */
public class PolishLanguageComponents implements LanguageComponentsProvider {
  public static final String NAME = "Polish";

  @Override
  public Set<String> languages() {
    return Collections.singleton(NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup) throws IOException {
    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, this::createStemmer);
    components.put(Tokenizer.class, ExtendedWhitespaceTokenizer::new);

    String langPrefix = language.toLowerCase(Locale.ROOT);
    LexicalData lexicalData = new LexicalDataImpl(resourceLookup,
        langPrefix + ".stopwords.utf8",
        langPrefix + ".stoplabels.utf8", true);
    components.put(LexicalData.class, () -> lexicalData);

    return components;
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, new ClassRelativeResourceLoader(this.getClass()));
  }

  private Stemmer createStemmer() {
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
  public String name() {
    return "Carrot2 (Polish)";
  }
}
