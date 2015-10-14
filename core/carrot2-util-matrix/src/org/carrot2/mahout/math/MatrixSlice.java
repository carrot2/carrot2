/* removed */

package org.carrot2.mahout.math;

public class MatrixSlice {

  private final Vector v;
  private final int index;

  public MatrixSlice(Vector v, int index) {
    this.v = v;
    this.index = index;
  }

  public Vector vector() { return v; }
  public int index() { return index; }
}

