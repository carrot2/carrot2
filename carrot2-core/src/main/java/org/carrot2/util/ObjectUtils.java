package org.carrot2.util;

public class ObjectUtils {
  public static <T> T firstNonNull(T o1, T o2) {
    if (o1 != null) {
      return o1;
    }
    if (o2 == null) {
      throw new RuntimeException("Both arguments null.");
    }
    return o2;
  }
}
