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
package org.carrot2.math.mahout;

public class MatrixSlice {

  private final Vector v;
  private final int index;

  public MatrixSlice(Vector v, int index) {
    this.v = v;
    this.index = index;
  }

  public Vector vector() {
    return v;
  }

  public int index() {
    return index;
  }
}
