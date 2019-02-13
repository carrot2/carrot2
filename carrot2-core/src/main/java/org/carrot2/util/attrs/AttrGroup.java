package org.carrot2.util.attrs;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public final class AttrGroup {
  private Map<String, Consumer<AttrVisitor>> attrs = new LinkedHashMap<>();

  public <T extends AcceptingVisitor> AttrObject<T> register(String key, AttrObject<T> attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public AttrInteger register(String key, AttrInteger attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public AttrBoolean register(String key, AttrBoolean attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  private void checkNewKey(String key) {
    if (attrs.containsKey(key)) {
      throw new RuntimeException(String.format(Locale.ROOT,
          "An attribute with key '%s' already exists.", key));

    }
  }

  public void visit(AttrVisitor visitor) {
    attrs.values().forEach(c -> c.accept(visitor));
  }
}

