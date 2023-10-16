/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
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
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.util.ResourceLookup;

public final class LanguageComponentsLoader {
  private Set<String> languageRestrictions;
  private Function<LanguageComponentsProvider, ResourceLookup> resourceLookupModifier;
  private ClusteringAlgorithm[] algorithmRestriction;

  public LoadedLanguages load() throws IOException {
    return load(loadProvidersFromSpi(defaultSpiClassloader()));
  }

  public LoadedLanguages load(Map<String, List<LanguageComponentsProvider>> languageProviders)
      throws IOException {
    sanityCheck(languageProviders);

    // Apply restrictions.
    if (languageRestrictions != null) {
      languageProviders.keySet().retainAll(languageRestrictions);
    }

    Function<Set<Class<?>>, Set<Class<?>>> componentFilters = s -> s;
    if (algorithmRestriction != null) {
      Set<Class<?>> components =
          Arrays.stream(algorithmRestriction)
              .flatMap(
                  algorithm ->
                      Stream.concat(
                          algorithm.optionalLanguageComponents().stream(),
                          algorithm.requiredLanguageComponents().stream()))
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

    // If we only have one language to support, remove any loaded unsupported languages.
    // We can't do this in general because languages A and B may support a mutually
    // exclusive subset of languages.
    if (algorithmRestriction != null && algorithmRestriction.length == 1) {
      var algorithm = algorithmRestriction[0];
      preloadedSuppliers
          .entrySet()
          .removeIf(
              e -> {
                return !algorithm.supports(new LanguageComponents(e.getKey(), e.getValue()));
              });
    }

    return new LoadedLanguages(preloadedSuppliers);
  }

  /** Limits the loaded components to just those required by the given list of languages. */
  public LanguageComponentsLoader limitToLanguages(String... languages) {
    if (this.languageRestrictions != null) {
      throw new RuntimeException("Method can be set once.");
    }
    this.languageRestrictions = new HashSet<>(Arrays.asList(languages));
    return this;
  }

  /**
   * Limits the loaded components to just those required by the given set of algorithms.
   *
   * <p>Note that there is no guarantee that all algorithms will have all the required components:
   * the loaded set may contain a subset of the required components of each algorithm. This method
   * exists to prevent unnecessary resources from being resolved and loaded.
   *
   * @see ClusteringAlgorithm#supports(LanguageComponents)
   */
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

  public static Map<String, List<LanguageComponentsProvider>> loadProvidersFromSpi(
      ClassLoader... classloaders) {
    Map<String, List<LanguageComponentsProvider>> providers = new TreeMap<>();

    BiPredicate<LanguageComponentsProvider, LanguageComponentsProvider> sameProvider =
        (p1, p2) -> {
          return Objects.equals(p1.name(), p2.name())
              && Objects.equals(p1.getClass(), p2.getClass())
              && Objects.equals(p1.componentTypes(), p2.componentTypes())
              && Objects.equals(p1.languages(), p2.languages());
        };

    for (ClassLoader classLoader : classloaders) {
      for (LanguageComponentsProvider candidate :
          ServiceLoader.load(LanguageComponentsProvider.class, classLoader)) {
        for (String language : candidate.languages()) {
          List<LanguageComponentsProvider> existingProviders =
              providers.computeIfAbsent(language, key -> new ArrayList<>());

          if (existingProviders.stream()
              .noneMatch(existing -> sameProvider.test(existing, candidate))) {
            existingProviders.add(candidate);
          }
        }
      }
    }

    return providers;
  }
}
