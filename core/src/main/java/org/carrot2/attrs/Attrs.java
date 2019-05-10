package org.carrot2.attrs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        attr.set(safeCast(map.get(key), key, Integer.class));
      }
    }

    @Override
    public void visit(String key, AttrDouble attr) {
      if (map.containsKey(key)) {
        Number value = safeCast(map.get(key), key, Number.class);
        attr.set(value == null ? null : value.doubleValue());
      }
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.get(key), key, Boolean.class));
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
            safeCast(classToInstance.apply(type), key, attr.getInterfaceClass());
            value = attr.castAndSet(classToInstance.apply(type));
          } else {
            value = attr.castAndSet(attr.newDefaultValue());
            if (value == null) {
              throw new RuntimeException("Default instance supplier not provided for: " + key);
            }
          }
          value.accept(new FromMapVisitor(submap, classToInstance));
        }
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void visit(String key, AttrObjectArray<?> attr) {
      if (map.containsKey(key)) {
        Object[] array = (Object[]) map.get(key);
        if (array == null) {
          attr.set(null);
        } else {
          List<Object> entries = Arrays.stream(array).map(entry -> {
            Map<String, Object> submap = (Map<String, Object>) entry;
            if (submap == null) {
              return null;
            } else {
              submap = new LinkedHashMap<>(submap);
              AcceptingVisitor value;
              if (submap.containsKey(KEY_TYPE)) {
                String type = (String) submap.remove(KEY_TYPE);
                value = safeCast(classToInstance.apply(type), key, attr.getInterfaceClass());
              } else {
                value = attr.newDefaultEntryValue();
                if (value == null) {
                  throw new RuntimeException("Default instance supplier not provided for: " + key);
                }
              }
              value.accept(new FromMapVisitor(submap, classToInstance));
              return value;
            }
          }).collect(Collectors.toList());

          attr.castAndSet(entries);
        }
      }
    }

    @Override
    public void visit(String key, AttrEnum<? extends Enum<?>> attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.get(key), key, String.class));
      }
    }

    @Override
    public void visit(String key, AttrStringArray attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.get(key), key, String[].class));
      }
    }

    @Override
    public void visit(String key, AttrString attr) {
      if (map.containsKey(key)) {
        attr.set(safeCast(map.get(key), key, String.class));
      }
    }

    private <T> T safeCast(Object value, String key, Class<T> clazz) {
      if (value == null) {
        return null;
      } else {
        if (!clazz.isInstance(value)) {
          throw new IllegalArgumentException(String.format(Locale.ROOT,
              "Value at key '%s' should be an instance of '%s', but encountered class '%s': '%s'",
              key, clazz.getSimpleName(), value.getClass().getSimpleName(), value.toString()));
        }
        return clazz.cast(value);
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
    public void visit(String key, AttrObjectArray<?> attrImpl) {
      ensureNoExistingKey(map, key);

      List<? extends AcceptingVisitor> values = attrImpl.get();
      if (values != null) {
        Object[] array = values.stream().map(currentValue -> {
          Map<String, Object> submap = new LinkedHashMap<>();
          if (!attrImpl.isDefaultClass(currentValue)) {
            submap.put(KEY_TYPE, objectToClass.apply(currentValue));
          }
          currentValue.accept(new ToMapVisitor(submap, objectToClass));
          return submap;
        }).toArray();
        map.put(key, array);
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
    StringBuilder builder = new StringBuilder();
    StringBuilder indent = new StringBuilder();

    ob.accept(new AttrVisitor() {
      @Override
      public void visit(String key, AttrBoolean attr) {
        append(key, attr.get());
      }

      @Override
      public void visit(String key, AttrInteger attr) {
        append(key, attr.get());
      }

      @Override
      public void visit(String key, AttrDouble attr) {
        append(key, attr.get());
      }

      @Override
      public void visit(String key, AttrEnum<? extends Enum<?>> attr) {
        Enum<?> value = attr.get();
        append(key, value == null ? value : "\"" + value + '"');
      }

      @Override
      public void visit(String key, AttrString attr) {
        append(key, attr.get());
      }

      @Override
      public void visit(String key, AttrStringArray attr) {
        String[] value = attr.get();
        if (value == null) {
          append(key, value);
        } else {
          builder.append(indent).append(key).append(": [\n");
          for (String v : value) {
            builder.append(indent).append("  \"").append(v).append("\"\n");
          }
          builder.append(indent).append("]\n");
        }
      }

      @Override
      public void visit(String key, AttrObject<?> attr) {
        AcceptingVisitor value = attr.get();
        if (value == null) {
          append(key, value);
        } else {
          builder.append(indent).append(key).append(": {\n");
          int len = indent.length();
          indent.append("  ");
          value.accept(this);
          indent.setLength(len);
          builder.append(indent).append("}\n");
        }
      }

      @Override
      public void visit(String key, AttrObjectArray<?> attr) {
        List<? extends AcceptingVisitor> value = attr.get();
        if (value == null) {
          append(key, value);
        } else {
          int len1 = indent.length();
          builder.append(indent).append(key).append(": [\n");
          indent.append("  ");
          for (AcceptingVisitor v : value) {
            builder.append(indent).append("{\n");
            int len2 = indent.length();
            indent.append("  ");
            v.accept(this);
            indent.setLength(len2);
            builder.append(indent).append("}\n");
          }
          indent.setLength(len1);
          builder.append(indent).append("]\n");
        }
      }

      private void append(String key, Object value) {
        builder.append(indent).append(key).append(": ").append(value).append('\n');
      }
    });

    return builder.toString();
  }
}
