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

public interface AttrVisitor {
  void visit(String key, AttrBoolean attr);

  void visit(String key, AttrInteger attr);

  void visit(String key, AttrDouble attr);

  void visit(String key, AttrString attr);

  void visit(String key, AttrStringArray attr);

  <T extends Enum<T>> void visit(String key, AttrEnum<T> attr);

  <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr);

  <T extends AcceptingVisitor> void visit(String key, AttrObjectArray<T> attr);
}
