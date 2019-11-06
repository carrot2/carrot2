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

import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.ClassNameMapper;

public class ClassInfoCollector {
  public static ClassInfo collect(AcceptingVisitor c, ClassNameMapper aliasMapper) {
    final ClassInfo ci = new ClassInfo();
    ci.name = aliasMapper.toName(c);
    ci.type = c.getClass().getName();
    c.accept(new AttrInfoCollector(ci.attributes, aliasMapper));
    return ci;
  }
}
