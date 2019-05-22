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
