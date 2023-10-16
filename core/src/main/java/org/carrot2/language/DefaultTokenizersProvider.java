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
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;

public class DefaultTokenizersProvider implements LanguageComponentsProvider {
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
    return Collections.singleton(Tokenizer.class);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(
      String language, ResourceLookup resourceLookup, Set<Class<?>> componentTypes)
      throws IOException {
    if (!componentTypes().equals(componentTypes)) {
      throw new IllegalArgumentException();
    }

    return Map.of(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
  }

  @Override
  public String name() {
    return "Carrot2 Core (Tokenizers)";
  }
}
