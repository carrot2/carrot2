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
package org.carrot2.infra.docattrs;

import java.util.HashMap;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.ClassNameMapper;

public class ClassInfoCollector {
  private final ClassNameMapper aliasMapper;
  private HashMap<Class<? extends AcceptingVisitor>, ClassInfo> collected = new HashMap<>();

  public ClassInfoCollector(ClassNameMapper aliasMapper) {
    this.aliasMapper = aliasMapper;
  }

  public ClassInfo collect(AcceptingVisitor c) {
    return collected.computeIfAbsent(
        c.getClass(),
        (key) -> {
          final ClassInfo ci = new ClassInfo();
          ci.clazz = c.getClass();
          ci.name = aliasMapper.toName(c);
          ci.type = getQualifiedName(c.getClass());

          c.accept(new AttrInfoCollector(ci.attributes, aliasMapper));
          return ci;
        });
  }

  public static String getQualifiedName(Class<?> clazz) {
    return clazz.getName().replace('$', '.');
  }
}
