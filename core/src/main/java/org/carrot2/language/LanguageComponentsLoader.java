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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.util.ResourceLookup;

public final class LanguageComponentsLoader {
  private ClassLoader spiClassLoader;
  private Set<String> languageRestrictions;
  private Function<LanguageComponentsProvider, ResourceLookup> resourceLookupModifier;
  private ClusteringAlgorithm[] algorithmRestriction;

  public LoadedLanguages load() throws IOException {
    if (spiClassLoader == null) {
      spiClassLoader = defaultSpiClassloader();
    }

    Map<String, List<LanguageComponentsProvider>> languageProviders =
        loadProvidersFromSpi(spiClassLoader);

    sanityCheck(languageProviders);

    // Apply restrictions.
    if (languageRestrictions != null) {
      languageProviders.keySet().retainAll(languageRestrictions);
    }

    Function<Set<Class<?>>, Set<Class<?>>> componentFilters = s -> s;
    if (algorithmRestriction != null) {
      Set<Class<?>> components =
          Arrays.stream(algorithmRestriction)
              .flatMap(algorithm -> algorithm.requiredLanguageComponents().stream())
              .collect(Collectors.toSet());
      componentFilters =
          fullSet -> {
            HashSet<Class<?>> needed = new HashSet<>(fullSet);
            needed.retainAll(components);
            return needed;
          };
    }

    // Preload components.
    Map<String, Map<Class<?>, Supplier<?>>> preloadedSuppliers = new LinkedHashMap<>();
    for (String language : languageProviders.keySet()) {
      LinkedHashMap<Class<?>, Supplier<?>> componentSuppliers = new LinkedHashMap<>();
      for (LanguageComponentsProvider provider : languageProviders.get(language)) {
        ResourceLookup rl;
        if (resourceLookupModifier != null) {
          rl = resourceLookupModifier.apply(provider);
        } else {
          rl = provider.defaultResourceLookup();
        }

        Set<Class<?>> requiredTypes = componentFilters.apply(provider.componentTypes());
        if (!requiredTypes.isEmpty()) {
          componentSuppliers.putAll(provider.load(language, rl, requiredTypes));
        }
      }

      if (!componentSuppliers.isEmpty()) {
        preloadedSuppliers.put(language, componentSuppliers);
      }
    }

    return new LoadedLanguages(preloadedSuppliers);
  }

  public LanguageComponentsLoader limitToLanguages(String... languages) {
    if (this.languageRestrictions != null) {
      throw new RuntimeException("Method can be set once.");
    }
    this.languageRestrictions = new HashSet<>(Arrays.asList(languages));
    return this;
  }

  public LanguageComponentsLoader limitToAlgorithms(ClusteringAlgorithm... algorithms) {
    if (this.algorithmRestriction != null) {
      throw new RuntimeException("Method can be set once.");
    }
    this.algorithmRestriction = algorithms;
    return this;
  }

  public LanguageComponentsLoader withResourceLookup(
      Function<LanguageComponentsProvider, ResourceLookup> resourceLookupModifier) {
    if (this.resourceLookupModifier != null) {
      throw new RuntimeException("Method can be set once.");
    }
    this.resourceLookupModifier = resourceLookupModifier;
    return this;
  }

  private void sanityCheck(Map<String, List<LanguageComponentsProvider>> languageProviders) {
    languageProviders.forEach(
        (language, providers) -> {
          Map<Class<?>, LanguageComponentsProvider> byComponent = new HashMap<>();
          providers.forEach(
              provider -> {
                for (Class<?> componentType : provider.componentTypes()) {
                  LanguageComponentsProvider previous = byComponent.put(componentType, provider);
                  if (previous != null) {
                    throw new RuntimeException(
                        String.format(
                            Locale.ROOT,
                            "Language '%s' has multiple providers of component '%s': %s",
                            language,
                            componentType.getSimpleName(),
                            Stream.of(previous, provider)
                                .map(s -> s.getClass().getName())
                                .collect(Collectors.joining(", "))));
                  }
                }
              });
        });
  }

  private ClassLoader defaultSpiClassloader() {
    return getClass().getClassLoader();
  }

  private Map<String, List<LanguageComponentsProvider>> loadProvidersFromSpi(
      ClassLoader spiClassLoader) {
    Map<String, List<LanguageComponentsProvider>> providers = new TreeMap<>();

    for (LanguageComponentsProvider provider :
        ServiceLoader.load(LanguageComponentsProvider.class, spiClassLoader)) {
      for (String language : provider.languages()) {
        providers.computeIfAbsent(language, key -> new ArrayList<>()).add(provider);
      }
    }

    return providers;
  }
}
