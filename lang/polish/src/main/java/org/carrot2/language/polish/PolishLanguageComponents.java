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
package org.carrot2.language.polish;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import morfologik.stemming.WordData;
import morfologik.stemming.polish.PolishStemmer;
import org.carrot2.language.*;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.ClassRelativeResourceLoader;
import org.carrot2.util.ResourceLookup;

/** */
public class PolishLanguageComponents implements LanguageComponentsProvider {
  public static final String NAME = "Polish";

  @Override
  public Set<String> languages() {
    return Collections.singleton(NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, this::createStemmer);
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
    return load(language, new ClassRelativeResourceLoader(this.getClass()));
  }

  private Stemmer createStemmer() {
    PolishStemmer stemmer = new PolishStemmer();
    return (word) -> {
      final List<WordData> stems = stemmer.lookup(word);
      if (stems == null || stems.isEmpty()) {
        return null;
      } else {
        return stems.get(0).getStem().toString();
      }
    };
  }

  @Override
  public String name() {
    return "Carrot2 (Polish)";
  }
}
