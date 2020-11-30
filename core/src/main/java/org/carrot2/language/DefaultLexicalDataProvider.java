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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;

public class DefaultLexicalDataProvider implements LanguageComponentsProvider {
  @Override
  public Set<String> languages() {
    return DefaultStemmersProvider.STEMMER_SUPPLIERS.keySet();
  }

  @Override
  public ResourceLookup defaultResourceLookup() {
    return new ClassRelativeResourceLookup(this.getClass());
  }

  @Override
  public Set<Class<?>> componentTypes() {
    return Set.of(WordFilter.class, LabelFilter.class);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(
      String language, ResourceLookup resourceLookup, Set<Class<?>> componentTypes)
      throws IOException {
    if (!componentTypes().equals(componentTypes)) {
      throw new IllegalArgumentException();
    }

    String langPrefix = language.toLowerCase(Locale.ROOT);

    // Load and precompile legacy defaults.
    return Map.of(
        WordFilter.class, legacyPlainTextWordFilter(langPrefix, resourceLookup),
        LabelFilter.class, legacyPlainTextLabelFilter(langPrefix, resourceLookup));
  }

  @Override
  public String name() {
    return "Carrot2 Lexical Data ("
        + String.join(", ", DefaultStemmersProvider.STEMMER_SUPPLIERS.keySet())
        + ")";
  }

  public static Supplier<LabelFilter> legacyPlainTextLabelFilter(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    LabelFilter labelFilter =
        RegExpLabelFilter.loadFromPlainText(
                resourceLookup, langPrefix.toLowerCase(Locale.ROOT) + ".stoplabels.utf8")
            .get();
    return () -> labelFilter;
  }

  public static Supplier<WordFilter> legacyPlainTextWordFilter(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    WordFilter wordFilter =
        WordListFilter.loadFromPlainText(
                resourceLookup, langPrefix.toLowerCase(Locale.ROOT) + ".stopwords.utf8")
            .get();
    return () -> wordFilter;
  }
}
