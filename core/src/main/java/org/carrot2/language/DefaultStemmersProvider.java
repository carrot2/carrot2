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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.language.snowball.DanishStemmer;
import org.carrot2.language.snowball.DutchStemmer;
import org.carrot2.language.snowball.EnglishStemmer;
import org.carrot2.language.snowball.FinnishStemmer;
import org.carrot2.language.snowball.FrenchStemmer;
import org.carrot2.language.snowball.GermanStemmer;
import org.carrot2.language.snowball.HungarianStemmer;
import org.carrot2.language.snowball.ItalianStemmer;
import org.carrot2.language.snowball.NorwegianStemmer;
import org.carrot2.language.snowball.PortugueseStemmer;
import org.carrot2.language.snowball.RomanianStemmer;
import org.carrot2.language.snowball.RussianStemmer;
import org.carrot2.language.snowball.SpanishStemmer;
import org.carrot2.language.snowball.SwedishStemmer;
import org.carrot2.language.snowball.TurkishStemmer;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;

public class DefaultStemmersProvider implements LanguageComponentsProvider {
  static final Map<String, Supplier<Stemmer>> STEMMER_SUPPLIERS;

  static {
    Map<String, Supplier<Stemmer>> m = new LinkedHashMap<>();
    m.put("English", () -> new SnowballStemmerAdapter(new EnglishStemmer()));
    m.put("Danish", () -> new SnowballStemmerAdapter(new DanishStemmer()));
    m.put("Dutch", () -> new SnowballStemmerAdapter(new DutchStemmer()));
    m.put("Finnish", () -> new SnowballStemmerAdapter(new FinnishStemmer()));
    m.put("French", () -> new SnowballStemmerAdapter(new FrenchStemmer()));
    m.put("German", () -> new SnowballStemmerAdapter(new GermanStemmer()));
    m.put("Hungarian", () -> new SnowballStemmerAdapter(new HungarianStemmer()));
    m.put("Italian", () -> new SnowballStemmerAdapter(new ItalianStemmer()));
    m.put("Norwegian", () -> new SnowballStemmerAdapter(new NorwegianStemmer()));
    m.put("Portuguese", () -> new SnowballStemmerAdapter(new PortugueseStemmer()));
    m.put("Romanian", () -> new SnowballStemmerAdapter(new RomanianStemmer()));
    m.put("Russian", () -> new SnowballStemmerAdapter(new RussianStemmer()));
    m.put("Spanish", () -> new SnowballStemmerAdapter(new SpanishStemmer()));
    m.put("Swedish", () -> new SnowballStemmerAdapter(new SwedishStemmer()));
    m.put("Turkish", () -> new SnowballStemmerAdapter(new TurkishStemmer()));
    STEMMER_SUPPLIERS = m;
  }

  @Override
  public Set<String> languages() {
    return STEMMER_SUPPLIERS.keySet();
  }

  @Override
  public ResourceLookup defaultResourceLookup() {
    return new ClassRelativeResourceLookup(this.getClass());
  }

  @Override
  public Set<Class<?>> componentTypes() {
    return Collections.singleton(Stemmer.class);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(
      String language, ResourceLookup resourceLookup, Set<Class<?>> componentTypes)
      throws IOException {
    if (!componentTypes().equals(componentTypes)) {
      throw new IllegalArgumentException(
          String.format(
              Locale.ROOT,
              "Invalid set of requested components (%s) with respect to supported components: %s",
              componentTypes,
              componentTypes()));
    }

    return Map.of(Stemmer.class, STEMMER_SUPPLIERS.get(language));
  }

  @Override
  public String name() {
    return "Carrot2 Core (Stemmers)";
  }
}
