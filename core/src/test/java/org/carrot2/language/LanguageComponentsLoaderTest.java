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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.attrs.AttrVisitor;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.Document;
import org.junit.Test;

public class LanguageComponentsLoaderTest extends TestBase {
  @Test
  public void testMultipleSpiDeduplicatesProviders() {
    Map<String, List<LanguageComponentsProvider>> langs =
        LanguageComponentsLoader.loadProvidersFromSpi(
            getClass().getClassLoader(), getClass().getClassLoader());

    for (List<LanguageComponentsProvider> perLangProviders : langs.values()) {
      perLangProviders.stream()
          .collect(Collectors.groupingBy(LanguageComponentsProvider::getClass))
          .values()
          .forEach(
              providersByClass -> {
                Assertions.assertThat(providersByClass).hasSize(1);
              });
    }
  }

  @Test
  public void testAlgorithmRestriction() throws IOException {
    class ExampleAlgorithm implements ClusteringAlgorithm {
      private final Set<Class<?>> requiredComponents;
      private final Set<Class<?>> optionalComponents;

      ExampleAlgorithm(Set<Class<?>> requiredComponents, Set<Class<?>> optionalComponents) {
        this.requiredComponents = requiredComponents;
        this.optionalComponents = optionalComponents;
      }

      @Override
      public void accept(AttrVisitor visitor) {}

      @Override
      public Set<Class<?>> requiredLanguageComponents() {
        return requiredComponents;
      }

      @Override
      public Set<Class<?>> optionalLanguageComponents() {
        return optionalComponents;
      }

      @Override
      public <T extends Document> List<Cluster<T>> cluster(
          Stream<? extends T> documents, LanguageComponents languageComponents) {
        return Collections.emptyList();
      }
    }

    class ExampleProvider extends SingleLanguageComponentsProviderImpl {
      protected ExampleProvider(String language, Class<?>... components) {
        super("example provider: " + language, language);
        for (Class<?> c : components) {
          registerResourceless(
              c,
              () -> {
                throw new RuntimeException();
              });
        }
      }
    }

    var a = (new Object() {}).getClass();
    var b = (new Object() {}).getClass();
    var c = (new Object() {}).getClass();
    var d = (new Object() {}).getClass();
    var e = (new Object() {}).getClass();
    var f = (new Object() {}).getClass();

    // One algorithm.
    {
      Map<String, List<LanguageComponentsProvider>> componentProviders =
          Map.of(
              "a", List.of(new ExampleProvider("a", a)),
              "ab", List.of(new ExampleProvider("ab", a, b)),
              "abd", List.of(new ExampleProvider("ab", a, b, d)),
              "ef", List.of(new ExampleProvider("ae", e, f)));

      // required: A, B; optional: C
      var algorithm = new ExampleAlgorithm(Set.of(a, b), Set.of(c));
      var loaded =
          LanguageComponents.loader().limitToAlgorithms(algorithm).load(componentProviders);
      Assertions.assertThat(loaded.languages()).containsOnly("ab", "abd");
      Assertions.assertThat(
              loaded.languages().stream().filter(lang -> algorithm.supports(loaded.language(lang))))
          .containsOnly("ab", "abd");
    }

    // Many algorithms; one resulting in insufficient required components.
    {
      Map<String, List<LanguageComponentsProvider>> componentProviders =
          Map.of(
              "l1", List.of(new ExampleProvider("a", a), new ExampleProvider("b", b)),
              "l2", List.of(new ExampleProvider("b", b), new ExampleProvider("c", c)));

      // required: A, B; optional: C
      var a1 = new ExampleAlgorithm(Set.of(a, b), Set.of());
      var a2 = new ExampleAlgorithm(Set.of(b, c), Set.of());

      var loaded = LanguageComponents.loader().limitToAlgorithms(a1, a2).load(componentProviders);
      Assertions.assertThat(loaded.languages()).containsOnly("l1", "l2");

      Assertions.assertThat(a1.supports(loaded.language("l1"))).isTrue();
      Assertions.assertThat(a1.supports(loaded.language("l2"))).isFalse();
      Assertions.assertThat(a2.supports(loaded.language("l1"))).isFalse();
      Assertions.assertThat(a2.supports(loaded.language("l2"))).isTrue();
    }
  }
}
