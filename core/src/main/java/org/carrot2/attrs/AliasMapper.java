package org.carrot2.attrs;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AliasMapper implements ClassNameMapper {
  public static ClassNameMapper SPI_DEFAULTS;

  static {
    SPI_DEFAULTS = loadFromSpi();
  }

  public static class Alias<T> {
    private final String name;
    private final Predicate<Object> isInstanceOf;
    private final Supplier<T> supplier;

    public Alias(String name, Predicate<Object> isInstanceOf, Supplier<T> supplier) {
      this.name = name;
      this.supplier = supplier;
      this.isInstanceOf = isInstanceOf;
    }
  }

  private final Map<String, Alias<?>> aliases = new LinkedHashMap<>();

  public <T> AliasMapper alias(String alias, Class<? extends T> exactClass, Supplier<T> supplier) {
    Objects.requireNonNull(exactClass);
    Objects.requireNonNull(supplier);
    alias(alias, new Alias<T>(alias, (ob) -> exactClass.equals(ob.getClass()), supplier));
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
            .filter(alias -> alias.isInstanceOf.test(value))
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

  private static AliasMapper loadFromSpi() {
    AliasMapper composite = new AliasMapper();
    HashMap<String, String> keyToFactoryName = new HashMap<>();
    for (AliasMappingFactory factory : ServiceLoader.load(AliasMappingFactory.class)) {
      String name = factory.name();
      factory
          .mapper()
          .aliases
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
