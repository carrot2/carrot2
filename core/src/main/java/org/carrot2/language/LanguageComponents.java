/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.util.ResourceLookup;

/** A set of language-specific components. */
public final class LanguageComponents {
  private static ClassLoader defaultSpiClassloader = LanguageComponents.class.getClassLoader();

  private final String language;
  private final Map<Class<?>, Supplier<?>> suppliers;

  public LanguageComponents(String language, Map<Class<?>, Supplier<?>> suppliers) {
    this.language = language;
    this.suppliers = suppliers;
  }

  public String language() {
    return language;
  }

  public <T> T get(Class<T> componentClass) {
    Supplier<?> supplier = suppliers.get(componentClass);
    if (supplier == null) {
      throw new RuntimeException(
          String.format(
              Locale.ROOT,
              "Language %s does not come with a supplier of component class %s.",
              language,
              componentClass.getName()));
    }
    return componentClass.cast(supplier.get());
  }

  public <T> LanguageComponents override(Class<T> clazz, Supplier<? extends T> supplier) {
    Map<Class<?>, Supplier<?>> clonedSuppliers = new LinkedHashMap<>(suppliers);
    clonedSuppliers.put(clazz, supplier);
    return new LanguageComponents(language, clonedSuppliers);
  }

  public Set<Class<?>> components() {
    return suppliers.keySet();
  }

  public static Set<String> languages() {
    return loadProvidersFromSpi(defaultSpiClassloader).keySet();
  }

  public static LanguageComponents load(String language) {
    try {
      return load(language, defaultSpiClassloader, (lang, provider) -> provider.load(lang));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static LanguageComponents load(String language, ResourceLookup resourceLookup)
      throws IOException {
    return load(
        language,
        defaultSpiClassloader,
        (lang, provider) -> provider.load(lang, Objects.requireNonNull(resourceLookup)));
  }

  public static LanguageComponents load(
      String language, ClassLoader cl, ResourceLookup resourceLookup) throws IOException {
    return load(
        language,
        cl,
        (lang, provider) -> provider.load(lang, Objects.requireNonNull(resourceLookup)));
  }

  public static LanguageComponents load(String language, ClassLoader cl, ComponentLoader loader)
      throws IOException {
    Map<String, List<LanguageComponentsProvider>> providers = loadProvidersFromSpi(cl);

    if (!providers.containsKey(language)) {
      throw new RuntimeException(
          String.format(
              Locale.ROOT,
              "Language components not available for language: %s [available: %s]",
              language,
              String.join(", ", providers.keySet())));
    }

    LinkedHashMap<Class<?>, Supplier<?>> componentSuppliers = new LinkedHashMap<>();
    for (LanguageComponentsProvider provider : providers.get(language)) {
      loader
          .load(language, provider)
          .forEach(
              (clazz, supplier) -> {
                Supplier<?> existing;
                if ((existing = componentSuppliers.put(clazz, supplier)) != null) {
                  throw new RuntimeException(
                      String.format(
                          Locale.ROOT,
                          "Language '%s' has multiple providers of component '%s': %s",
                          language,
                          clazz.getSimpleName(),
                          Stream.of(existing, supplier)
                              .map(s -> s.getClass().getName())
                              .collect(Collectors.joining(", "))));
                }
              });
    }

    return new LanguageComponents(language, componentSuppliers);
  }

  private static Map<ClassLoader, Map<String, List<LanguageComponentsProvider>>> SPI_PROVIDERS =
      Collections.synchronizedMap(new WeakHashMap<>());

  public static Map<String, List<LanguageComponentsProvider>> loadProvidersFrom(ClassLoader cl) {
    Map<String, List<LanguageComponentsProvider>> providers = new LinkedHashMap<>();

    for (LanguageComponentsProvider provider :
        ServiceLoader.load(LanguageComponentsProvider.class, cl)) {
      for (String language : provider.languages()) {
        providers.compute(
            language,
            (k, v) -> {
              if (v == null) {
                v = new ArrayList<>();
              }
              v.add(provider);
              return v;
            });
      }
    }

    return providers;
  }

  private static synchronized Map<String, List<LanguageComponentsProvider>> loadProvidersFromSpi(
      ClassLoader cl) {
    return SPI_PROVIDERS.computeIfAbsent(cl, (key) -> loadProvidersFrom(cl));
  }
}
