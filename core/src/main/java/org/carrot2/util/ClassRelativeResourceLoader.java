package org.carrot2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class ClassRelativeResourceLoader implements ResourceLookup {
  private final Class<?> clazz;

  public ClassRelativeResourceLoader(Class<?> clazz) {
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
