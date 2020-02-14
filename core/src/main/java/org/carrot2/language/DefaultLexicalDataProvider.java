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
import java.util.Collections;
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
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    String langPrefix = language.toLowerCase(Locale.ROOT);
    LexicalData lexicalData =
        new LexicalDataImpl(
            resourceLookup, langPrefix + ".stopwords.utf8", langPrefix + ".stoplabels.utf8");

    return Collections.singletonMap(LexicalData.class, () -> lexicalData);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, new ClassRelativeResourceLookup(this.getClass()));
  }

  @Override
  public String name() {
    return "Carrot2 Lexical Data ("
        + String.join(", ", DefaultStemmersProvider.STEMMER_SUPPLIERS.keySet())
        + ")";
  }
}
