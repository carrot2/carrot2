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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.carrot2.util.ClassRelativeResourceLookup;
import org.carrot2.util.ResourceLookup;

public abstract class SingleLanguageComponentsProviderImpl implements LanguageComponentsProvider {
  private final String language;
  private final String providerName;
  private final LinkedHashMap<Class<?>, SupplierLoader<?>> components = new LinkedHashMap<>();

  protected interface SupplierLoader<T> {
    Supplier<T> get(String language, ResourceLookup resourceLookup) throws IOException;
  }

  protected SingleLanguageComponentsProviderImpl(String providerName, String language) {
    this.language = language;
    this.providerName = providerName;
  }

  @Override
  public String name() {
    return providerName;
  }

  @Override
  public final Set<String> languages() {
    return Collections.singleton(language);
  }

  @Override
  public ResourceLookup defaultResourceLookup() {
    return new ClassRelativeResourceLookup(getClass());
  }

  @Override
  public Set<Class<?>> componentTypes() {
    return components.keySet();
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(
      String language, ResourceLookup resourceLookup, Set<Class<?>> componentTypes)
      throws IOException {

    LinkedHashMap<Class<?>, Supplier<?>> result = new LinkedHashMap<>();
    for (Class<?> componentType : componentTypes) {
      SupplierLoader<?> supplierLoader = components.get(componentType);
      if (supplierLoader == null) {
        throw new IllegalArgumentException("Not a registered component: " + componentType);
      }
      result.put(componentType, supplierLoader.get(language, resourceLookup));
    }
    return result;
  }

  protected final <T> void register(Class<T> clazz, SupplierLoader<? extends T> loader) {
    if (components.containsKey(clazz)) {
      throw new IllegalArgumentException("Component class already registered: " + clazz);
    }
    components.put(clazz, loader);
  }

  protected final <T> void registerResourceless(Class<T> clazz, Supplier<T> supplier) {
    register(clazz, (language, resourceLoader) -> supplier);
  }

  protected final void registerDefaultLexicalData() {
    register(StopwordFilter.class, DefaultLexicalDataProvider::readDefaultWordFilters);
    register(LabelFilter.class, DefaultLexicalDataProvider::readDefaultLabelFilters);
  }
}
