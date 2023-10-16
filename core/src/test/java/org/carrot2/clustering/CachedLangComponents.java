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
package org.carrot2.clustering;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.carrot2.language.LanguageComponents;
import org.carrot2.language.LoadedLanguages;

public class CachedLangComponents {
  private static LoadedLanguages spiDefaults;

  static {
    try {
      spiDefaults = LanguageComponents.loader().load();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static LanguageComponents english() {
    return loadCached("English");
  }

  public static LanguageComponents loadCached(String language) {
    return spiDefaults.language(language);
  }
}
