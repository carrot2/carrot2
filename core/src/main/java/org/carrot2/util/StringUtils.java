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

import java.util.Iterator;

/** */
public final class StringUtils {
  private StringUtils() {}

  public static <T> String toString(Iterable<T> iterable, String separator) {
    final StringBuilder stringBuilder = new StringBuilder();

    for (final Iterator<T> iterator = iterable.iterator(); iterator.hasNext(); ) {
      final T object = iterator.next();
      stringBuilder.append(object);
      if (iterator.hasNext()) {
        stringBuilder.append(separator);
      }
    }

    return stringBuilder.toString();
  }

  public static boolean isNullOrEmpty(String s) {
    return s == null || s.isEmpty();
  }

  public static boolean isNotBlank(String s) {
    return !isBlank(s);
  }

  public static boolean isEmpty(String s) {
    return s == null || s.isEmpty();
  }

  public static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}
