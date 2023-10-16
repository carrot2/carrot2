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

import java.util.Objects;

/** An immutable pair of objects. */
public class Pair<I, J> {
  public final I objectA;
  public final J objectB;

  public Pair(I clazz, J parameter) {
    this.objectA = clazz;
    this.objectB = parameter;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Pair)) {
      return false;
    }

    final Pair<?, ?> other = (Pair<?, ?>) obj;

    return Objects.equals(other.objectA, objectA) && Objects.equals(other.objectB, objectB);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectA, objectB);
  }
}
