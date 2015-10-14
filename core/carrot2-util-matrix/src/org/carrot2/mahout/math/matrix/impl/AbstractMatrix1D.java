/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.matrix.impl;

/* removed */

public abstract class AbstractMatrix1D extends AbstractMatrix {

  /* removed */
  protected int size;
  /* removed */
  protected int zero;
  /* removed */
  protected int stride;

  /* removed */
  protected AbstractMatrix1D() {
  }

  /* removed */
  protected int offset(int absRank) {
    return absRank;
  }

  /* removed */
  protected int rank(int rank) {
    return zero + rank * stride;
    //return zero + ((rank+flipMask)^flipMask);
    //return zero + rank*flip; // slower
  }

  /* removed */
  protected void checkIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Attempted to access at index=" + index);
    }
  }

  /* removed */
  protected void checkRange(int index, int width) {
    if (index < 0 || index + width > size) {
      throw new IndexOutOfBoundsException("index: " + index + ", width: " + width + ", size=" + size);
    }
  }

  /* removed */
  public void checkSize(AbstractMatrix1D b) {
    if (size != b.size) {
      throw new IllegalArgumentException("Incompatible sizes: " + size + " and " + b.size);
    }
  }

  /* removed */
  protected int index(int rank) {
    return offset(rank(rank));
  }

  /* removed */
  protected void setUp(int size) {
    setUp(size, 0, 1);
  }

  /* removed */
  protected void setUp(int size, int zero, int stride) {
    if (size < 0) {
      throw new IllegalArgumentException("negative size");
    }

    this.size = size;
    this.zero = zero;
    this.stride = stride;
    this.isNoView = true;
  }

  /* removed */
  @Override
  public int size() {
    return size;
  }

  /* removed */
  protected int stride(int dimension) {
    if (dimension != 0) {
      throw new IllegalArgumentException("invalid dimension: " + dimension);
    }
    return this.stride;
  }

  /* removed */
  protected AbstractMatrix1D vPart(int index, int width) {
    checkRange(index, width);
    this.zero += this.stride * index;
    this.size = width;
    this.isNoView = false;
    return this;
  }

}
