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

public class LanguageComponentsProviderImpl implements LanguageComponentsProvider {
  private final String language;
  private final String providerName;

  protected LanguageComponentsProviderImpl(String providerName, String language) {
    this.language = language;
    this.providerName = providerName;
  }

  @Override
  public String name() {
    return providerName;
  }

  @Override
  public Set<String> languages() {
    return Collections.singleton(language);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    return null;
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language) throws IOException {
    return load(language, new ClassRelativeResourceLookup(this.getClass()));
  }

  protected static final LexicalData loadLexicalData(String language, ResourceLookup resourceLookup)
      throws IOException {
    String langPrefix = language.toLowerCase(Locale.ROOT);
    return new LexicalDataImpl(
        resourceLookup, langPrefix + ".stopwords.utf8", langPrefix + ".stoplabels.utf8");
  }
}
