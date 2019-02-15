package org.carrot2.language;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A set of language-specific components. Instances not thread safe and cannot be shared
 * across different threads. Default implementations for named
 * languages available via {@link #get(String)}}.
 *
 * @see #get(String)
 */
public class LanguageComponents {
  private static Map<String, LanguageComponentsFactory> factories;

  public Stemmer stemmer;
  public Tokenizer tokenizer;
  public LexicalData lexicalData;

  public LanguageComponents(Stemmer stemmer, Tokenizer tokenizer, LexicalData lexicalData) {
    this.stemmer = stemmer;
    this.tokenizer = tokenizer;
    this.lexicalData = lexicalData;
  }

  public synchronized static Set<String> languages() {
    return factories().map(factory -> factory.name())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static Stream<LanguageComponentsFactory> factories() {
    checkInitialized();
    return factories.values().stream();
  }

  public static LanguageComponents get(String name) {
    checkInitialized();
    LanguageComponentsFactory factory = factories.get(name);
    if (factory == null) {
      throw new RuntimeException("Language components not available for language: " + name);
    }
    return new LanguageComponents(
        factory.createStemmer(),
        factory.createTokenizer(),
        factory.createLexicalResources()
    );
  }

  private synchronized static void checkInitialized() {
    if (factories == null) {
      factories = loadFromSpi();
    }
  }

  private static Map<String, LanguageComponentsFactory> loadFromSpi() {
    Map<String, LanguageComponentsFactory> factories = new LinkedHashMap<>();
    for (LanguageComponentsFactory factory : ServiceLoader.load(LanguageComponentsFactory.class)) {
      String name = factory.name();
      if (factories.containsKey(name)) {
        throw new RuntimeException(String.format(Locale.ROOT,
            "Duplicate implementation of interface %s for language %s: %s and %s.",
            LanguageComponentsFactory.class.getName(),
            name,
            factory.getClass().getName(),
            factories.get(name).getClass().getName()));
      }
      factories.put(name, factory);
    }
    return factories;
  }
}
