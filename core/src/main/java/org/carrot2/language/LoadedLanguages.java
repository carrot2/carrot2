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

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class LoadedLanguages {
  private final Map<String, Map<Class<?>, Supplier<?>>> preloadedSuppliers;

  LoadedLanguages(Map<String, Map<Class<?>, Supplier<?>>> preloadedSuppliers) {
    this.preloadedSuppliers = preloadedSuppliers;
  }

  public LanguageComponents language(String language) {
    if (!preloadedSuppliers.containsKey(language)) {
      throw new RuntimeException(
          String.format(
              Locale.ROOT,
              "Language components not available for language: %s [available: %s]",
              language,
              String.join(", ", languages())));
    }

    return new LanguageComponents(language, preloadedSuppliers.get(language));
  }

  public Set<String> languages() {
    return preloadedSuppliers.keySet();
  }
}
