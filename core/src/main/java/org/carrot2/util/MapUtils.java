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

import java.util.*;

/** Utilities for working with {@link Map}s. */
public class MapUtils {
  private MapUtils() {}

  public static <K, V> HashMap<K, V> asHashMap(Map<K, V> map) {
    if (map instanceof HashMap) {
      return (HashMap<K, V>) map;
    } else {
      return new HashMap<K, V>(map);
    }
  }

  public static <K, V> Map<K, V> mapOf(K k1, V v1) {
    HashMap<K, V> map = new HashMap<>();
    map.put(k1, v1);
    return Collections.unmodifiableMap(map);
  }

  public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
    HashMap<K, V> map = new HashMap<>();
    map.put(k1, v1);
    map.put(k2, v2);
    return Collections.unmodifiableMap(map);
  }
}
