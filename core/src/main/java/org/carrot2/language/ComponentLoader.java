package org.carrot2.language;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

public interface ComponentLoader {
  Map<Class<?>, Supplier<?>> load(String language, LanguageComponentsProvider provider)
      throws IOException;
}
