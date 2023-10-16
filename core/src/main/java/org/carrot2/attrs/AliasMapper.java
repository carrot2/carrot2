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
package org.carrot2.attrs;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class AliasMapper implements ClassNameMapper {
  public static ClassNameMapper SPI_DEFAULTS;

  static {
    SPI_DEFAULTS = loadFromSpi(AliasMapper.class.getClassLoader());
  }

  public static class Alias<T> {
    private final String name;
    private final Class<? extends T> exactClass;
    private final Supplier<T> supplier;

    public Alias(String name, Class<? extends T> clazz, Supplier<T> supplier) {
      this.name = name;
      this.supplier = supplier;
      this.exactClass = clazz;
    }

    boolean isInstanceOf(Object value) {
      return exactClass.equals(value.getClass());
    }

    public Class<? extends T> ofClass() {
      return exactClass;
    }
  }

  private final Map<String, Alias<?>> aliases = new LinkedHashMap<>();

  public final Map<String, Alias<?>> aliases() {
    return Collections.unmodifiableMap(aliases);
  }

  public <T> AliasMapper alias(String alias, Class<? extends T> exactClass, Supplier<T> supplier) {
    Objects.requireNonNull(exactClass);
    Objects.requireNonNull(supplier);

    alias(alias, new Alias<T>(alias, exactClass, supplier));
    return this;
  }

  <T> void alias(String key, Alias<T> alias) {
    if (aliases.containsKey(alias)) {
      throw new RuntimeException(
          String.format(Locale.ROOT, "An alias of key '%s' already exists.", alias));
    }
    aliases.put(key, alias);
  }

  @Override
  public Object fromName(String name) {
    Objects.requireNonNull(name);

    if (!aliases.containsKey(name)) {
      throw new RuntimeException(
          String.format(Locale.ROOT, "Could not locate alias supplier for class name: %s", name));
    }

    return aliases.get(name).supplier.get();
  }

  @Override
  public String toName(Object value) {
    Objects.requireNonNull(value);
    Optional<String> first =
        aliases.values().stream()
            .filter(alias -> alias.isInstanceOf(value))
            .map(alias -> alias.name)
            .findFirst();

    if (!first.isPresent()) {
      throw new RuntimeException(
          String.format(
              Locale.ROOT,
              "Could not find a name alias for an instance of class: %s",
              value.getClass().getName()));
    }

    return first.get();
  }

  public static AliasMapper loadFromSpi(ClassLoader cl) {
    AliasMapper composite = new AliasMapper();
    HashMap<String, String> keyToFactoryName = new HashMap<>();
    for (AliasMapperFactory factory : ServiceLoader.load(AliasMapperFactory.class, cl)) {
      String name = factory.name();
      factory
          .mapper()
          .aliases()
          .forEach(
              (key, alias) -> {
                if (keyToFactoryName.containsKey(key)) {
                  throw new RuntimeException(
                      String.format(
                          Locale.ROOT,
                          "Class alias named '%s' already defined by more than one factory: %s, %s",
                          key,
                          name,
                          keyToFactoryName.get(key)));
                }
                keyToFactoryName.put(key, name);
                composite.alias(key, alias);
              });
    }
    return composite;
  }
}
