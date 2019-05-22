package org.carrot2.attrs;

public class AttrAccess {
  /** Bypass preconditions. */
  public static <T> void forceSet(Attr<? super T> attr, T value) {
    attr.value = value;
  }
}
