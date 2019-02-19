package org.carrot2.attrs;

import java.util.function.Supplier;

public interface AttrVisitor {
  void visit(String key, AttrBoolean attr);
  void visit(String key, AttrInteger attr);
  void visit(String key, AttrDouble attr);
  void visit(String key, AttrObject<?> attr);
  void visit(String key, AttrEnum<? extends Enum<?>> attr);
  void visit(String key, AttrString attr);
  void visit(String key, AttrStringArray attr);

  <T extends AcceptingVisitor> void visit(String key, T value, Supplier<T> creator);
}
