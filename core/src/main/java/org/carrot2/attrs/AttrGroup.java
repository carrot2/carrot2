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

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public final class AttrGroup {
  private Map<String, Consumer<AttrVisitor>> attrs = new TreeMap<>();

  public <T extends AcceptingVisitor> AttrObject<T> register(String key, AttrObject<T> attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public <T extends AcceptingVisitor> AttrObjectArray<T> register(
      String key, AttrObjectArray<T> attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public <T extends Enum<T>> AttrEnum<T> register(String key, AttrEnum<T> attr) {
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

  public AttrDouble register(String key, AttrDouble attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public AttrString register(String key, AttrString attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  public AttrStringArray register(String key, AttrStringArray attr) {
    checkNewKey(key);
    attrs.put(key, (visitor) -> visitor.visit(key, attr));
    return attr;
  }

  private void checkNewKey(String key) {
    if (attrs.containsKey(key)) {
      throw new RuntimeException(
          String.format(Locale.ROOT, "An attribute with key '%s' already exists.", key));
    }
  }

  public void visit(AttrVisitor visitor) {
    attrs.values().forEach(c -> c.accept(visitor));
  }
}
