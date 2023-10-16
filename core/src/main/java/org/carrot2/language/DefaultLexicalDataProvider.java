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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.attrs.Attrs;
import org.carrot2.internal.nanojson.JsonParser;
import org.carrot2.internal.nanojson.JsonParserException;
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
    return Set.of(StopwordFilter.class, LabelFilter.class);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(
      String language, ResourceLookup resourceLookup, Set<Class<?>> componentTypes)
      throws IOException {
    if (!componentTypes().equals(componentTypes)) {
      throw new IllegalArgumentException();
    }

    String langPrefix = language.toLowerCase(Locale.ROOT);
    return Map.of(
        StopwordFilter.class, readDefaultWordFilters(langPrefix, resourceLookup),
        LabelFilter.class, readDefaultLabelFilters(langPrefix, resourceLookup));
  }

  @Override
  public String name() {
    return "Carrot2 Core (Lexical Data)";
  }

  public static Supplier<LabelFilter> readDefaultLabelFilters(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    langPrefix = langPrefix.toLowerCase(Locale.ROOT);

    String filterDictionary = String.format(Locale.ROOT, "%s.label-filters.json", langPrefix);
    try (InputStream is = resourceLookup.open(filterDictionary);
        Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      try {
        LabelFilterDictionary dictionary =
            Attrs.fromMap(
                LabelFilterDictionary.class,
                DefaultDictionaryImpl::new,
                JsonParser.object().from(reader));
        LabelFilter labelFilter = dictionary.compileLabelFilter();
        return () -> labelFilter;
      } catch (JsonParserException e) {
        throw new IOException(
            String.format(
                Locale.ROOT,
                "JSON syntax exception in dictionary %s: %s",
                filterDictionary,
                e.getMessage()),
            e);
      }
    }
  }

  public static Supplier<StopwordFilter> readDefaultWordFilters(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    langPrefix = langPrefix.toLowerCase(Locale.ROOT);

    String filterDictionary = String.format(Locale.ROOT, "%s.word-filters.json", langPrefix);
    try (InputStream is = resourceLookup.open(filterDictionary);
        Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      try {
        StopwordFilterDictionary dictionary =
            Attrs.fromMap(
                StopwordFilterDictionary.class,
                DefaultDictionaryImpl::new,
                JsonParser.object().from(reader));
        StopwordFilter labelFilter = dictionary.compileStopwordFilter();
        return () -> labelFilter;
      } catch (JsonParserException e) {
        throw new IOException(
            String.format(
                Locale.ROOT,
                "JSON syntax exception in dictionary %s: %s",
                filterDictionary,
                e.getMessage()),
            e);
      }
    }
  }
}
