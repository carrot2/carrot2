package org.carrot2.language;

import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TestsSpecializedEnglishComponent implements LanguageComponentsProvider {
  @Override
  public Set<String> languages() {
    return Collections.singleton("English");
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup) throws IOException {
    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Runnable.class, (Supplier<Runnable>) () -> () -> {});
    return components;
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, null);
  }
}
