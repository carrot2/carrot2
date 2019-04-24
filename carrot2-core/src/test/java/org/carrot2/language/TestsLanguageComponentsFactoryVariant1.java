package org.carrot2.language;

import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TestsLanguageComponentsFactoryVariant1 implements LanguageComponentsProvider {
  public static final String NAME = "_tests_language_";

  @Override
  public Set<String> languages() {
    return Collections.singleton(NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup) throws IOException {
    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, (Supplier<Stemmer>) () -> (word) -> null);
    components.put(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    components.put(LexicalData.class, () -> new LexicalData() {});
    return components;
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, null);
  }

  @Override
  public String name() {
    return getClass().getName();
  }
}
