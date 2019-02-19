package org.carrot2.attrs;

public interface AcceptingVisitor {
  void accept(AttrVisitor visitor);
}
