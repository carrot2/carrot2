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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.internal.nanojson.JsonWriter;

/**
 * Static utility methods for converting between {@link AcceptingVisitor} and {@link Map}s.
 *
 * @see #extract(AcceptingVisitor)
 * @see #populate(AcceptingVisitor, Map)
 * @see #toMap(AcceptingVisitor)
 * @see #fromMap(Class, Map, Function)
 */
public final class Attrs {
  private static final String KEY_WRAPPED = "@value";
  static final String KEY_TYPE = "@type";

  private static class Wrapper<E extends AcceptingVisitor> implements AcceptingVisitor {
    final AttrObject<E> value;

    public Wrapper(Class<E> clazz, Supplier<? extends E> defaultInstanceSupplier) {
      value = AttrObject.builder(clazz).defaultValue(defaultInstanceSupplier);
    }

    @Override
    public void accept(AttrVisitor visitor) {
      visitor.visit(KEY_WRAPPED, value);
    }
  }

  /**
   * Convert an instance to a map. The output map will contain the type of the instance and can be
   * recreated with {@link #fromMap(Class, Map)}.
   *
   * <p>This method uses default class name mappings.
   *
   * @see #fromMap(Class, Map)
   */
  public static Map<String, Object> toMap(AcceptingVisitor composite) {
    return toMap(composite, AliasMapper.SPI_DEFAULTS::toName);
  }

  /**
   * Convert an instance to a map. The output map will contain the type of the instance and can be
   * recreated with {@link #fromMap(Class, Map)}.
   *
   * @param classToName Class to name mapping provider.
   * @see #fromMap(Class, Map, Function)
   */
  public static Map<String, Object> toMap(
      AcceptingVisitor composite, Function<Object, String> classToName) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    Wrapper<AcceptingVisitor> wrapped = new Wrapper<>(AcceptingVisitor.class, () -> null);
    wrapped.value.set(composite);
    wrapped.accept(new ToMapVisitor(map, classToName));

    @SuppressWarnings("unchecked")
    Map<String, Object> sub = (Map<String, Object>) map.get(KEY_WRAPPED);
    return sub;
  }

  /**
   * Convert a map to an instance of a class.
   *
   * <p>This method uses default class name mappings.
   *
   * @see #toMap(AcceptingVisitor)
   */
  public static <E extends AcceptingVisitor> E fromMap(
      Class<? extends E> clazz, Map<String, Object> map) {
    return fromMap(clazz, map, AliasMapper.SPI_DEFAULTS::fromName);
  }

  /**
   * Convert a map to an instance of a class.
   *
   * @param nameToClass Name to new class instance supplier.
   * @see #toMap(AcceptingVisitor)
   */
  public static <E extends AcceptingVisitor> E fromMap(
      Class<E> clazz, Map<String, Object> map, Function<String, Object> nameToClass) {
    Wrapper<E> wrapper =
        populate(new Wrapper<>(clazz, () -> null), Map.of(KEY_WRAPPED, map), nameToClass);
    return safeCast(wrapper.value.get(), KEY_WRAPPED, clazz);
  }

  /**
   * Convert a map to an instance of a class.
   *
   * @param nameToClass Name to new class instance supplier.
   * @see #toMap(AcceptingVisitor)
   * @since 4.1.0
   */
  public static <E extends AcceptingVisitor> E fromMap(
      Class<E> clazz,
      Supplier<? extends E> defaultImplSupplier,
      Map<String, Object> map,
      Function<String, Object> nameToClass) {
    Wrapper<E> wrapper =
        populate(new Wrapper<>(clazz, defaultImplSupplier), Map.of(KEY_WRAPPED, map), nameToClass);
    return safeCast(wrapper.value.get(), KEY_WRAPPED, clazz);
  }

  /**
   * Convert a map to an instance of a class.
   *
   * @see #toMap(AcceptingVisitor)
   * @since 4.1.0
   */
  public static <E extends AcceptingVisitor> E fromMap(
      Class<E> clazz, Supplier<? extends E> defaultImplSupplier, Map<String, Object> map) {
    return fromMap(clazz, defaultImplSupplier, map, AliasMapper.SPI_DEFAULTS::fromName);
  }

  /**
   * Extracts just the attributes of an instance (no top-level type information is preserved).
   *
   * @param classToName Class to name mapping provider.
   */
  public static Map<String, Object> extract(
      AcceptingVisitor instance, Function<Object, String> classToName) {
    Map<String, Object> attrs = toMap(instance, classToName);
    attrs.remove(KEY_TYPE);
    return attrs;
  }

  /**
   * Extracts just the attributes of an instance (no top-level type information is preserved).
   *
   * <p>This method uses default class name mappings.
   */
  public static Map<String, Object> extract(AcceptingVisitor instance) {
    return extract(instance, AliasMapper.SPI_DEFAULTS::toName);
  }

  /**
   * Populates a given instance with the values from the map.
   *
   * <p>This method uses default class name mappings.
   *
   * @see #extract(AcceptingVisitor)
   */
  public static <E extends AcceptingVisitor> E populate(E instance, Map<String, Object> map) {
    return populate(instance, map, AliasMapper.SPI_DEFAULTS::fromName);
  }

  /**
   * Populates a given instance with the values from the map.
   *
   * @param nameToClass Name to new class instance supplier.
   * @see #extract(AcceptingVisitor, Function)
   */
  public static <E extends AcceptingVisitor> E populate(
      E instance, Map<String, Object> map, Function<String, Object> nameToClass) {
    FromMapVisitor visitor = new FromMapVisitor(map, nameToClass);
    instance.accept(visitor);
    visitor.ensureKeysConsumed();

    return instance;
  }

  /**
   * Converts an instance (recursively) to JSON.
   *
   * <p>This method uses default class name mappings ({@link AliasMapper#SPI_DEFAULTS}).
   *
   * @since 4.1.0
   */
  public static String toJson(AcceptingVisitor composite) {
    return toJson(composite, AliasMapper.SPI_DEFAULTS);
  }

  /** Converts an instance (recursively) to JSON. */
  public static String toJson(AcceptingVisitor composite, ClassNameMapper classNameMapper) {
    return toJson(composite, classNameMapper::toName);
  }

  /**
   * Converts an instance (recursively) to JSON.
   *
   * @param classToName Class to name mapping provider.
   */
  public static String toJson(AcceptingVisitor composite, Function<Object, String> classToName) {
    Map<String, Object> asMap = toMap(composite, classToName);
    return JsonWriter.indent("  ").string().object(asMap).done();
  }

  private static class FromMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;
    private final Function<String, Object> classToInstance;

    private FromMapVisitor(Map<String, Object> map, Function<String, Object> classToInstance) {
      this.map = new LinkedHashMap<>(Objects.requireNonNull(map));
      this.classToInstance = classToInstance;
    }

    @Override
    public void visit(String key, AttrInteger attr) {
      if (map.containsKey(key)) {
        Number value = safeCast(map.remove(key), key, Number.class);
        if (value != null) {
          if (Double.compare(value.doubleValue(), value.longValue()) != 0) {
            throw new IllegalArgumentException(
                String.format(
                    Locale.ROOT,
                    "Value at key '%s' should be an integer but encountered floating point value: '%s'",
                    key,
                    toDebugString(value)));
          }

          long v = value.longValue();
          if ((int) v != v) {
            throw new IllegalArgumentException(
                String.format(
                    Locale.ROOT,
                    "Value at key '%s' should be an integer but is out of integer range: '%s'",
                    key,
                    toDebugString(value)));
          }
          attr.set((int) v);
        } else {
          attr.set(null);
        }
      }
    }

    @Override
    public void visit(String key, AttrDouble attr) {
      if (map.containsKey(key)) {
        Number value = safeCast(map.remove(key), key, Number.class);
        attr.set(value == null ? null : value.doubleValue());
      }
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.remove(key), key, Boolean.class));
      }
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
      if (map.containsKey(key)) {
        @SuppressWarnings("unchecked")
        Map<String, Object> submap = safeCast(this.map.remove(key), key, Map.class);
        if (submap == null) {
          attr.set(null);
        } else {
          submap = new LinkedHashMap<>(submap);
          T value;
          if (submap.containsKey(KEY_TYPE)) {
            String valueType = safeCast(submap.remove(KEY_TYPE), KEY_TYPE, String.class);
            value = safeCast(classToInstance.apply(valueType), key, attr.getInterfaceClass());
          } else {
            value = notNull(key, attr.newDefaultValue());
          }

          FromMapVisitor visitor = new FromMapVisitor(submap, classToInstance);
          value.accept(visitor);
          visitor.ensureKeysConsumed();

          attr.set(value);
        }
      }
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
      if (map.containsKey(key)) {
        Object value = map.remove(key);
        if (value == null) {
          attr.set(null);
        } else {
          List<?> array;
          if (value instanceof List) {
            array = (List<?>) value;
          } else if (value instanceof Object[]) {
            array = Arrays.asList((Object[]) value);
          } else {
            throw new IllegalArgumentException(
                String.format(
                    Locale.ROOT,
                    "Value at key '%s' should be a list or an array of objects of type '%s' but is an instance of '%s': '%s'",
                    key,
                    attr.getInterfaceClass().getSimpleName(),
                    value.getClass().getSimpleName(),
                    toDebugString(value)));
          }

          List<T> entries =
              array.stream()
                  .map(
                      entry -> {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> submap = safeCast(entry, key, Map.class);
                        if (submap == null) {
                          return null;
                        } else {
                          submap = new LinkedHashMap<>(submap);
                          T entryValue;
                          if (submap.containsKey(KEY_TYPE)) {
                            String entryValueType =
                                safeCast(submap.remove(KEY_TYPE), KEY_TYPE, String.class);
                            entryValue =
                                safeCast(
                                    classToInstance.apply(entryValueType),
                                    key,
                                    attr.getInterfaceClass());
                          } else {
                            entryValue = notNull(key, attr.newDefaultEntryValue());
                          }

                          FromMapVisitor visitor = new FromMapVisitor(submap, classToInstance);
                          entryValue.accept(visitor);
                          visitor.ensureKeysConsumed();

                          return entryValue;
                        }
                      })
                  .collect(Collectors.toList());

          attr.set(entries);
        }
      }
    }

    @Override
    public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
      if (map.containsKey(key)) {
        Object value = map.remove(key);
        if (value == null) {
          attr.set((T) null);
        } else if (value instanceof String) {
          try {
            attr.set(Enum.valueOf(attr.enumClass(), (String) value));
          } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                String.format(
                    Locale.ROOT,
                    "Value at key '%s' should be an enum constant of class '%s', but no such constant exists: '%s' (available constants: %s)",
                    key,
                    attr.enumClass().getSimpleName(),
                    toDebugString(value),
                    EnumSet.allOf(attr.enumClass())));
          }
        } else {
          attr.set(safeCast(value, key, attr.enumClass()));
        }
      }
    }

    @Override
    public void visit(String key, AttrStringArray attr) {
      if (map.containsKey(key)) {
        Object value = map.remove(key);
        if (value instanceof Object[]) {
          // Attempt to convert Object[] to String[].
          attr.set(
              Stream.of((Object[]) value)
                  .map(ob -> safeCast(ob, key, String.class))
                  .toArray(String[]::new));
        } else if (value instanceof List<?>) {
          // Attempt to convert List<?> to String[].
          attr.set(
              ((List<?>) value)
                  .stream().map(ob -> safeCast(ob, key, String.class)).toArray(String[]::new));
        } else {
          attr.set(safeCast(value, key, String[].class));
        }
      }
    }

    @Override
    public void visit(String key, AttrString attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.remove(key), key, String.class));
      }
    }

    private <T> T notNull(String key, T value) {
      if (value == null) {
        throw new RuntimeException(
            String.format(
                Locale.ROOT,
                "Default attribute implementation supplier returned null for key: '%s'",
                key));
      }
      return value;
    }

    public void ensureKeysConsumed() {
      if (!map.isEmpty()) {
        throw new IllegalArgumentException(
            String.format(
                Locale.ROOT,
                "Unrecognized extra attributes with keys: %s",
                String.join(", ", map.keySet())));
      }
    }
  }

  static <T> T safeCast(Object value, String key, Class<T> clazz) {
    if (value == null) {
      return null;
    } else {
      if (!clazz.isInstance(value)) {
        throw new IllegalArgumentException(
            String.format(
                Locale.ROOT,
                "Value at key '%s' should be an instance of '%s', but encountered class '%s': '%s'",
                key,
                clazz.getSimpleName(),
                value.getClass().getSimpleName(),
                toDebugString(value)));
      }
      return clazz.cast(value);
    }
  }

  static String toDebugString(Object value) {
    if (value == null) {
      return "[null]";
    } else if (value instanceof Object[]) {
      return Arrays.deepToString(((Object[]) value));
    } else {
      return Objects.toString(value);
    }
  }

  private static class ToMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;
    private final Function<Object, String> objectToClass;

    public ToMapVisitor(Map<String, Object> map, Function<Object, String> objectToClass) {
      this.map = map;
      this.objectToClass = objectToClass;
    }

    @Override
    public void visit(String key, AttrInteger attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get());
    }

    @Override
    public void visit(String key, AttrDouble attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get());
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get());
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
      ensureNoExistingKey(map, key);
      AcceptingVisitor currentValue = attr.get();
      if (currentValue != null) {
        Map<String, Object> submap = new LinkedHashMap<>();
        if (!attr.isDefaultClass(currentValue)) {
          submap.put(KEY_TYPE, objectToClass.apply(currentValue));
        }
        currentValue.accept(new ToMapVisitor(submap, objectToClass));
        map.put(key, submap);
      } else {
        map.put(key, null);
      }
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
      ensureNoExistingKey(map, key);

      List<T> values = attr.get();
      if (values != null) {
        Object[] array =
            values.stream()
                .map(
                    currentValue -> {
                      Map<String, Object> submap = new LinkedHashMap<>();
                      if (!attr.isDefaultClass(currentValue)) {
                        submap.put(KEY_TYPE, objectToClass.apply(currentValue));
                      }
                      currentValue.accept(new ToMapVisitor(submap, objectToClass));
                      return submap;
                    })
                .toArray();
        map.put(key, array);
      } else {
        map.put(key, null);
      }
    }

    @Override
    public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get() != null ? attr.get().name() : null);
    }

    @Override
    public void visit(String key, AttrStringArray attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get());
    }

    @Override
    public void visit(String key, AttrString attr) {
      ensureNoExistingKey(map, key);
      map.put(key, attr.get());
    }

    private void ensureNoExistingKey(Map<?, ?> map, String key) {
      if (map.containsKey(key)) {
        throw new RuntimeException(
            String.format(
                Locale.ROOT,
                "Could not serialize key '%s' because it already exists in the map with value: %s",
                key,
                map.get(key)));
      }
    }
  }
}
