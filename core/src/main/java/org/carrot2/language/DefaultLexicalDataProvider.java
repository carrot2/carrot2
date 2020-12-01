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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
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

    // Load and precompile legacy defaults.
    return Map.of(
        StopwordFilter.class, legacyPlainTextWordFilter(langPrefix, resourceLookup),
        LabelFilter.class, legacyPlainTextLabelFilter(langPrefix, resourceLookup));
  }

  @Override
  public String name() {
    return "Carrot2 Core (Lexical Data)";
  }

  public static Supplier<LabelFilter> legacyPlainTextLabelFilter(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    DefaultDictionaryImpl attr = new DefaultDictionaryImpl();
    Set<String> regexps =
        readLines(resourceLookup, langPrefix.toLowerCase(Locale.ROOT) + ".stoplabels.utf8");
    attr.regexp.set(regexps.toArray(String[]::new));
    LabelFilter labelFilter = attr.compileLabelFilter();
    return () -> labelFilter;
  }

  public static Supplier<StopwordFilter> legacyPlainTextWordFilter(
      String langPrefix, ResourceLookup resourceLookup) throws IOException {
    DefaultDictionaryImpl attr = new DefaultDictionaryImpl();
    Set<String> words =
        readLines(resourceLookup, langPrefix.toLowerCase(Locale.ROOT) + ".stopwords.utf8");
    attr.exact.set(words.toArray(String[]::new));
    StopwordFilter wordFilter1 = attr.compileStopwordFilter();
    return () -> wordFilter1;
  }

  private static Set<String> readLines(ResourceLookup resourceLookup, String resource)
      throws IOException {
    try (InputStream is = resourceLookup.open(resource)) {
      return readLines(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)));
    }
  }

  /**
   * Loads words from a given resource (UTF-8, one word per line, #-starting lines are considered
   * comments).
   */
  private static Set<String> readLines(BufferedReader reader) throws IOException {
    final LinkedHashSet<String> words = new LinkedHashSet<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (!line.startsWith("#") && !line.isEmpty()) {
        words.add(line);
      }
    }
    return words;
  }
}
