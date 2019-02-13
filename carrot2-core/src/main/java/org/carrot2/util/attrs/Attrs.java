package org.carrot2.util.attrs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class Attrs {
  private final static String KEY_TYPE = "@type";
  private final static String KEY_WRAPPED = "@value";

  private static class Wrapper implements AcceptingVisitor {
    AttrObject<AcceptingVisitor> value = AttrObject.builder(AcceptingVisitor.class)
        .build();

    @Override
    public void accept(AttrVisitor visitor) {
      visitor.visit(KEY_WRAPPED, value);
    }
  }

  public static Map<String, Object> toMap(AcceptingVisitor composite, Function<Object, String> classToName) {

    LinkedHashMap<String, Object> map = new LinkedHashMap<>();

    Wrapper wrapped = new Wrapper();
    wrapped.value.set(composite);
    wrapped.accept(new ToMapVisitor(map, classToName));
    return (Map<String, Object>) map.get(KEY_WRAPPED);
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
      this.map = map;
      this.classToInstance = classToInstance;
    }

    @Override
    public void visit(String key, AttrInteger attr) {
      if (map.containsKey(key)) {
        attr.set((Integer) map.get(key));
      }
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      if (map.containsKey(key)) {
        attr.set((Boolean) map.get(key));
      }
    }

    @Override
    public void visit(String key, AttrObject<?> attrImpl) {
      if (map.containsKey(key)) {
        Map<String, Object> submap = new HashMap<>((Map<String, Object>) map.get(key));
        String type = (String) submap.remove(KEY_TYPE);
        attrImpl
            .castSet(classToInstance.apply(type))
            .accept(new FromMapVisitor(submap, classToInstance));
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
    public void visit(String key, AttrBoolean attr) {
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

    @Override
    public void visit(String key, AttrObject<?> attrImpl) {
      ensureNoExistingKey(map, key);
      AcceptingVisitor acceptingVisitor = attrImpl.get();
      if (acceptingVisitor != null) {
        Map<String, Object> submap = new LinkedHashMap<>();
        submap.put(KEY_TYPE, objectToClass.apply(acceptingVisitor));
        acceptingVisitor.accept(new ToMapVisitor(submap, objectToClass));
        map.put(key, submap);
      } else {
        map.put(key, null);
      }
    }
  }
}
