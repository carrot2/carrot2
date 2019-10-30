/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.infra.docattrs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.Attr;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrEnum;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrObjectArray;
import org.carrot2.attrs.AttrString;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.attrs.AttrVisitor;
import org.carrot2.attrs.Constraint;

public class AttrInfoCollector implements AttrVisitor {
  private final Map<String, Object> map;

  public AttrInfoCollector(Map<String, Object> map) {
    this.map = map;
  }

  @Override
  public void visit(String key, AttrBoolean attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", "Boolean");
  }

  @Override
  public void visit(String key, AttrInteger attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", "Integer");
  }

  @Override
  public void visit(String key, AttrDouble attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", "Double");
  }

  @Override
  public void visit(String key, AttrString attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", "String");
  }

  @Override
  public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", attr.enumClass().getName() + " (enum)");
  }

  @Override
  public void visit(String key, AttrStringArray attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", "String[]");
  }

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", attr.getInterfaceClass().getName());
    map.put(key, info);
  }

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
    Map<String, Object> info = attrInfo(key, attr);
    info.put("type", attr.getInterfaceClass().getName() + "[]");
    map.put(key, info);
  }

  private <T> Map<String, Object> attrInfo(String key, Attr<T> attr) {
    Map<String, Object> info = new LinkedHashMap<>();
    info.put("description", attr.getDescription());

    Object value = attr.get();
    if (attr instanceof AttrObject<?>) {
      if (value != null) {
        value = "instance of " + value.getClass().getName();
      }
    }

    info.put("value", value == null ? null : Objects.toString(value));

    if (map.containsKey(key)) {
      throw new RuntimeException("Duplicate attribute key unexpected: " + key);
    }

    List<Constraint<? super T>> constraints = attr.getConstraints();
    if (!constraints.isEmpty()) {
      info.put(
          "constraints",
          constraints.stream().map(Constraint::description).collect(Collectors.toList()));
    }

    map.put(key, info);
    return info;
  }
}
