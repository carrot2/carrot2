package org.carrot2.util.attrs;

public interface AcceptingVisitor {
  void accept(AttrVisitor visitor);
}
