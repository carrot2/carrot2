package org.carrot2.language;

import org.carrot2.util.ResourceLookup;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface LanguageComponentsProvider {
  Set<String> languages();
  Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup) throws IOException;
  Map<Class<?>, Supplier<?>> load(String language) throws IOException;
}
