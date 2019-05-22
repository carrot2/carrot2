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
