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
