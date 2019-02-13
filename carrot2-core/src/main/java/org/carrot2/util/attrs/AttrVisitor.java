package org.carrot2.util.attrs;

public interface AttrVisitor {
  void visit(String key, AttrBoolean attr);
  void visit(String key, AttrInteger attr);
  void visit(String key, AttrObject<?> attr);
}
