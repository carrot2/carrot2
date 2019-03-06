package org.carrot2.language;

import org.carrot2.util.ResourceLookup;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A set of language-specific components.
 */
public final class LanguageComponents {
  private final String language;
  private final Map<Class<?>, Supplier<?>> suppliers;

  public LanguageComponents(String language, LinkedHashMap<Class<?>, Supplier<?>> suppliers) {
    this.language = language;
    this.suppliers = suppliers;
  }

  public String language() {
    return language;
  }

  public <T> T get(Class<T> componentClass) {
    Supplier<?> supplier = suppliers.get(componentClass);
    if (supplier == null) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Language %s does not come with a supplier of component class %s.",
          language,
          componentClass.getName()));
    }
    return componentClass.cast(supplier.get());
  }

  public Set<Class<?>> components() {
    return suppliers.keySet();
  }

  public static Set<String> languages() {
    try {
      return loadProvidersFromSpi().keySet();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static LanguageComponents load(String language) {
    try {
      return loadImpl(language, null);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static LanguageComponents load(String language, ResourceLookup resourceLookup) throws IOException {
    return loadImpl(language, Objects.requireNonNull(resourceLookup));
  }

  private static LanguageComponents loadImpl(String language, ResourceLookup resourceLookup) throws IOException {
    Map<String, List<LanguageComponentsProvider>> providers = loadProvidersFromSpi();

    if (!providers.containsKey(language)) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Language components not available for language: %s [available: %s]",
          language,
          String.join(", ", providers.keySet())));
    }

    LinkedHashMap<Class<?>, Supplier<?>> componentSuppliers = new LinkedHashMap<>();
    for (LanguageComponentsProvider provider : providers.get(language)) {
      Map<Class<?>, Supplier<?>> suppliers =
          resourceLookup == null ? provider.load(language) : provider.load(language, resourceLookup);

      suppliers.forEach((clazz, supplier) -> {
            if (componentSuppliers.put(clazz, supplier) != null) {
              throw new RuntimeException(String.format(Locale.ROOT,
                  "Language %s has multiple providers of component %s?",
                  language,
                  clazz.getSimpleName()));
            }
          });
    }

    return new LanguageComponents(language, componentSuppliers);
  }

  private synchronized static Map<String, List<LanguageComponentsProvider>> loadProvidersFromSpi() throws IOException {
    Map<String, List<LanguageComponentsProvider>> providers = new LinkedHashMap<>();

    for (LanguageComponentsProvider provider : ServiceLoader.load(LanguageComponentsProvider.class)) {
      for (String language : provider.languages()) {
        providers.compute(language, (k, v) -> {
          if (v == null) {
            v = new ArrayList<>();
          }
          v.add(provider);
          return v;
        });
      }
    }

    // TODO: This performs a sanity check but may be too heavy if called repeatedly?
    for (Map.Entry<String, List<LanguageComponentsProvider>> e : providers.entrySet()) {
      String language = e.getKey();
      HashMap<Class<?>, LanguageComponentsProvider> components = new HashMap<>();
      for (LanguageComponentsProvider provider : e.getValue()) {
        for (Class<?> clazz : provider.load(language).keySet()) {
          if (components.containsKey(clazz)) {
            throw new RuntimeException(String.format(Locale.ROOT,
                "Language '%s' has multiple providers implementing component class %s: %s and %s.",
                language,
                clazz.getName(),
                provider.getClass().getName(),
                components.get(clazz).getClass().getName()));
          }
          components.put(clazz, provider);
        }
      }
    }
    return providers;
  }
}
