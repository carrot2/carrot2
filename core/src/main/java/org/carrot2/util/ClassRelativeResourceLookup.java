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
package org.carrot2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class ClassRelativeResourceLookup implements ResourceLookup {
  private final Class<?> clazz;

  public ClassRelativeResourceLookup(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public InputStream open(String resource) throws IOException {
    InputStream is = clazz.getResourceAsStream(Objects.requireNonNull(resource));
    if (is == null) {
      throw new IOException(
          String.format(
              Locale.ROOT,
              "Resource %s does not exist relative to class %s.",
              resource,
              clazz.getName()));
    }
    return is;
  }

  @Override
  public boolean exists(String resource) {
    return clazz.getResource(resource) != null;
  }

  @Override
  public String pathOf(String resource) {
    return clazz.getResource(resource).toExternalForm();
  }
}
