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

import java.util.Collection;
import java.util.LinkedHashMap;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrDouble;
import org.carrot2.attrs.AttrEnum;
import org.carrot2.attrs.AttrInteger;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.AttrObjectArray;
import org.carrot2.attrs.AttrString;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.attrs.AttrVisitor;

public class AttrTypeCollector implements AttrVisitor {

  private LinkedHashMap<Class<? extends AcceptingVisitor>, AcceptingVisitor> collectedTypes =
      new LinkedHashMap<>();

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr) {
    visit(attr.get());
  }

  @Override
  public <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr) {
    attr.get().forEach(this::visit);
  }

  public void visit(AcceptingVisitor value) {
    if (!collectedTypes.containsKey(value.getClass())) {
      collectedTypes.put(value.getClass(), value);
      value.accept(this);
    }
  }

  public Collection<AcceptingVisitor> collectedTypes() {
    return collectedTypes.values();
  }

  @Override
  public void visit(String key, AttrBoolean attr) {
    // Ignore.
  }

  @Override
  public void visit(String key, AttrInteger attr) {
    // Ignore.
  }

  @Override
  public void visit(String key, AttrDouble attr) {
    // Ignore.
  }

  @Override
  public void visit(String key, AttrString attr) {
    // Ignore.
  }

  @Override
  public void visit(String key, AttrStringArray attr) {
    // Ignore.
  }

  @Override
  public <T extends Enum<T>> void visit(String key, AttrEnum<T> attr) {
    // Ignore
  }
}
