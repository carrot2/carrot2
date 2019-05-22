/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.clustering;

import java.util.concurrent.ConcurrentHashMap;
import org.carrot2.language.LanguageComponents;

public class CachedLangComponents {
  private static ConcurrentHashMap<String, LanguageComponents> langCompCache =
      new ConcurrentHashMap<>();

  public static LanguageComponents english() {
    return loadCached("English");
  }

  public static LanguageComponents loadCached(String language) {
    return langCompCache.computeIfAbsent(language, LanguageComponents::load);
  }
}
