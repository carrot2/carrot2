package org.carrot2.attrs;

public interface AttrVisitor {
  void visit(String key, AttrBoolean attr);
  void visit(String key, AttrInteger attr);
  void visit(String key, AttrDouble attr);
  void visit(String key, AttrEnum<? extends Enum<?>> attr);
  void visit(String key, AttrString attr);
  void visit(String key, AttrStringArray attr);
  // <T extends AcceptingVisitor> void visit(String key, AttrObject<T> attr);
  void visit(String key, AttrObject<?> attr);
  void visit(String key, AttrObjectArray<?> attr);
}
