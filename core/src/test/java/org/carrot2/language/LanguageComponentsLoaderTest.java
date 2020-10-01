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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
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
}
