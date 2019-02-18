package org.carrot2.util.attrs;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AliasMapper implements ClassNameMapper {
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
    if (aliases.containsKey(alias)) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "An alias of key '%s' already exists.", alias));
    }

    aliases.put(alias, new Alias<T>(alias, (ob) -> exactClass.equals(ob.getClass()), supplier));
    return this;
  }

  @Override
  public Object fromName(String name) {
    Objects.requireNonNull(name);

    if (!aliases.containsKey(name)) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Could not locate alias supplier for class name: %s", name));
    }

    return aliases.get(name).supplier.get();
  }

  @Override
  public String toName(Object value) {
    Objects.requireNonNull(value);
    Optional<String> first = aliases.values().stream()
        .filter(alias -> alias.isInstanceOf.test(value))
        .map(alias -> alias.name)
        .findFirst();

    if (!first.isPresent()) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "Could not find a name alias for an instance of class: %s",
          value.getClass().getName()));
    }

    return first.get();
  }
}
