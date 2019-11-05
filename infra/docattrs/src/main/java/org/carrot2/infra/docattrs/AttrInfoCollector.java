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
import java.util.Optional;
import java.util.function.Supplier;
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
import org.carrot2.attrs.ClassNameMapper;
import org.carrot2.attrs.Constraint;

public class AttrInfoCollector {
  private final ClassNameMapper aliasMapper;
  private final List<Class<?>> classUniverse;

  public AttrInfoCollector(ClassNameMapper aliasMapper, List<Class<?>> classUniverse) {
    this.aliasMapper = aliasMapper;
    this.classUniverse = classUniverse;
  }

  public Map<String, Object> collect(AcceptingVisitor v) {
    Map<String, Object> classInfo = new LinkedHashMap<>();
    classInfo.put("name", aliasMapper.toName(v));
    classInfo.put("type", v.getClass().getName());

    Map<String, Object> attributes = new LinkedHashMap<>();
    classInfo.put("attributes", attributes);
    v.accept(new Visitor(attributes));

    return classInfo;
  }

  private class Visitor implements AttrVisitor {
    private final Map<String, Object> map;

    private Visitor(Map<String, Object> map) {
      this.map = map;
    }

    @Override
    public void visit(String key, AttrBoolean attr) {
      attrInfo(key, attr, () -> "Boolean");
    }

    @Override
    public void visit(String key, AttrInteger attr) {
      attrInfo(key, attr, () -> "Integer");
    }

    @Override
    public void visit(String key, AttrDouble attr) {
      attrInfo(key, attr, () -> "Double");
    }

    @Override
    public void visit(String key, AttrString attr) {
      attrInfo(key, attr, () -> "String");
    }

    @Override
    public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
      attrInfo(key, attr, () -> attr.enumClass().getName());
    }

    @Override
    public void visit(String key, AttrStringArray attr) {
      attrInfo(key, attr, () -> "String[]");
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
      Map<String, Object> info = attrInfo(key, attr, () -> attr.getInterfaceClass().getName());
      info.put("implementations", getImplementations(attr.getInterfaceClass()));
      map.put(key, info);
    }

    @Override
    public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
      attrInfo(key, attr, () -> attr.getInterfaceClass().getName() + "[]");
      if (attr.get() != null && !attr.get().isEmpty()) {
        throw new RuntimeException(
            "Don't know how to emit value for non-empty array attribute: " + key);
      }
    }

    private List<Map<String, String>> getImplementations(Class<?> parent) {
      return classUniverse.stream()
          .filter(impl -> !impl.isInterface())
          .filter(parent::isAssignableFrom)
          .map(
              impl ->
                  Map.of(
                      "name",
                      aliasMapper.toName(WriteDescriptorsCommand.getInstance(impl)),
                      "type",
                      impl.getName()))
          .collect(Collectors.toList());
    }

    private <T> Map<String, Object> attrInfo(String key, Attr<T> attr, Supplier<String> type) {
      Map<String, Object> info = new LinkedHashMap<>();
      info.put("description", attr.getDescription());
      info.put("type", type.get());

      List<Constraint<? super T>> constraints = attr.getConstraints();
      if (!constraints.isEmpty()) {
        info.put(
            "constraints",
            constraints.stream().map(Constraint::description).collect(Collectors.toList()));
      }

      Optional<Object> value = Optional.ofNullable(attr.get());
      if (attr instanceof AttrObject<?>) {
        value = value.map(aliasMapper::toName);
      } else {
        value = value.map(Objects::toString);
      }
      info.put("value", value.orElse(null));

      if (map.containsKey(key)) {
        throw new RuntimeException("Duplicate attribute key unexpected: " + key);
      }

      map.put(key, info);
      return info;
    }
  }
}
