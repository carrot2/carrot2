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
package org.carrot2.language;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import org.carrot2.language.snowball.*;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;

public class DefaultLanguageComponentsProvider implements LanguageComponentsProvider {
  private static final Map<String, Supplier<Stemmer>> STEMMER_SUPPLIERS;

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
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, STEMMER_SUPPLIERS.get(language));
    components.put(Tokenizer.class, ExtendedWhitespaceTokenizer::new);

    String langPrefix = language.toLowerCase(Locale.ROOT);
    LexicalData lexicalData =
        new LexicalDataImpl(
            resourceLookup, langPrefix + ".stopwords.utf8", langPrefix + ".stoplabels.utf8");
    components.put(LexicalData.class, () -> lexicalData);

    components.put(LabelFormatter.class, () -> new LabelFormatterImpl(" "));

    return components;
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, new ClassRelativeResourceLookup(this.getClass()));
  }

  @Override
  public String name() {
    return "Carrot2 (" + String.join(", ", STEMMER_SUPPLIERS.keySet()) + ")";
  }
}
