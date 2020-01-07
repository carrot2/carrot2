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
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.util.ResourceLookup;

public interface LanguageComponentsProvider {
  String name();

  Set<String> languages();

  Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException;

  Map<Class<?>, Supplier<?>> load(String language) throws IOException;
}
