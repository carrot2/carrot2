package org.carrot2.attrs;

import java.util.*;
import java.util.function.Function;

public final class Attrs {
  final static String KEY_TYPE = "@type";
  final static String KEY_WRAPPED = "@value";

  private static class Wrapper implements AcceptingVisitor {
    AttrObject<AcceptingVisitor> value = AttrObject.builder(AcceptingVisitor.class)
        .defaultValue(() -> null);

    @Override
    public void accept(AttrVisitor visitor) {
      visitor.visit(KEY_WRAPPED, value);
    }
  }

  public static Map<String, Object> toMap(AcceptingVisitor composite) {
    return toMap(composite, AliasMapper.SPI_DEFAULTS::toName);
  }

  public static Map<String, Object> toMap(AcceptingVisitor composite, Function<Object, String> classToName) {
    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    Wrapper wrapped = new Wrapper();
    wrapped.value.set(composite);
    wrapped.accept(new ToMapVisitor(map, classToName));

    @SuppressWarnings("unchecked")
    Map<String, Object> sub = (Map<String, Object>) map.get(KEY_WRAPPED);
    return sub;
  }

  public static <E extends AcceptingVisitor> E fromMap(Class<? extends E> clazz,
                                                       Map<String, Object> map) {
    return fromMap(clazz, map, AliasMapper.SPI_DEFAULTS::fromName);
  }

  public static <E extends AcceptingVisitor> E fromMap(Class<? extends E> clazz,
                                                       Map<String, Object> map,
                                                       Function<String, Object> nameToClass) {
    Map<String, Object> wrapped = new LinkedHashMap<>();
    wrapped.put(KEY_WRAPPED, map);

    Wrapper wrapper = new Wrapper();
    wrapper.accept(new FromMapVisitor(wrapped, nameToClass));
    return clazz.cast(wrapper.value.get());
  }

  public static class FromMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;
    private final Function<String, Object> classToInstance;

    public FromMapVisitor(Map<String, Object> map,
                          Function<String, Object> classToInstance) {
      this.map = Objects.requireNonNull(map);
      this.classToInstance = classToInstance;
    }

    @Override
    public void visit(String key, AttrInteger attr) {
      if (map.containsKey(key)) {
        Object value = map.get(key);
        if (value != null) {
          attr.set((Integer) map.get(key));
        } else {
          attr.set(null);
        }
      }
    }

    @Override
    public void visit(String key, AttrDouble attr) {
      if (map.containsKey(key)) {
        Number number = (Number) map.get(key);
        if (number != null) {
          attr.set(number.doubleValue());
        } else {
          attr.set(null);
        }
      }
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      if (map.containsKey(key)) {
        attr.set((Boolean) map.get(key));
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(String key, AttrObject<?> attr) {
      if (map.containsKey(key)) {
        Map<String, Object> submap = (Map<String, Object>) map.get(key);
        if (submap == null) {
          attr.set(null);
        } else {
          submap = new LinkedHashMap<>(submap);
          AcceptingVisitor value;
          if (submap.containsKey(KEY_TYPE)) {
            String type = (String) submap.remove(KEY_TYPE);
            value = attr.castAndSet(classToInstance.apply(type));
          } else {
            Object instance = attr.newDefaultValue();
            if (instance == null) {
              throw new RuntimeException("Default instance supplier not provided for: " + key);
            }
            value = attr.castAndSet(instance);
          }
          value.accept(new FromMapVisitor(submap, classToInstance));
        }
      }
    }

    @Override
    public void visit(String key, AttrEnum<? extends Enum<?>> attr) {
      if (map.containsKey(key)) {
        attr.set((String) map.get(key));
      }
    }

    @Override
    public void visit(String key, AttrStringArray attr) {
      if (map.containsKey(key)) {
        attr.set((String[]) map.get(key));
      }
    }

    @Override
    public void visit(String key, AttrString attr) {
      if (map.containsKey(key)) {
        attr.set((String) map.get(key));
      }
    }
  }

  private static class ToMapVisitor implements AttrVisitor {
    private final Map<String, Object> map;
    private final Function<Object, String> objectToClass;

    public ToMapVisitor(Map<String, Object> map,
                        Function<Object, String> objectToClass) {
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
    public void visit(String key, AttrObject<?> attrImpl) {
      ensureNoExistingKey(map, key);
      AcceptingVisitor currentValue = attrImpl.get();
      if (currentValue != null) {
        Map<String, Object> submap = new LinkedHashMap<>();
        if (!attrImpl.isDefaultClass(currentValue)) {
          submap.put(KEY_TYPE, objectToClass.apply(currentValue));
        }
        currentValue.accept(new ToMapVisitor(submap, objectToClass));
        map.put(key, submap);
      } else {
        map.put(key, null);
      }
    }

    @Override
    public void visit(String key, AttrEnum<? extends Enum<?>> attr) {
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
        throw new RuntimeException(String.format(Locale.ROOT,
            "Could not serialize key '%s' because it already exists in the map with value: %s",
            key,
            map.get(key)));
      }
    }
  }

  public static String toPrettyString(AcceptingVisitor ob, ClassNameMapper mapper) {
    Map<String, Object> map = Attrs.toMap(ob, mapper::toName);
    StringBuilder builder = new StringBuilder();
    StringBuilder indent = new StringBuilder();
    appendMap(map, builder, indent);
    return builder.toString();
  }

  private static void appendMap(Map<String, Object> map, StringBuilder builder, StringBuilder indent) {
    map.forEach((k, v) -> {
      builder.append(indent).append(k).append(": ");
      if (v instanceof Map) {
        builder.append("{\n");

        int len = indent.length();
        indent.append("  ");
        @SuppressWarnings("unchecked")
        Map<String, Object> vMap = (Map<String, Object>) v;
        appendMap(vMap, builder, indent.append(" "));
        indent.setLength(len);

        builder.append(indent);
        builder.append("}");
      } else if (v != null && v.getClass().isArray()) {
        if (v instanceof Object[]) {
          builder.append(Arrays.toString((Object[]) v));
        } else {
          throw new RuntimeException("Unexpected array type: " + v);
        }
      } else {
        builder.append(v);
      }
      builder.append("\n");
    });
  }
}
