/*
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

/* removed */
final class SelectedDenseDoubleMatrix1D extends DoubleMatrix1D {

  /* removed */
  final double[] elements;

  /* removed */
  private final int[] offsets;

  /* removed */
  private int offset;

  /* removed */
  SelectedDenseDoubleMatrix1D(double[] elements, int[] offsets) {
    this(offsets.length, elements, 0, 1, offsets, 0);
  }

  /* removed */
  SelectedDenseDoubleMatrix1D(int size, double[] elements, int zero, int stride, int[] offsets, int offset) {
    setUp(size, zero, stride);

    this.elements = elements;
    this.offsets = offsets;
    this.offset = offset;
    this.isNoView = false;
  }

  /* removed */
  @Override
  protected int offset(int absRank) {
    return offsets[absRank];
  }

  /* removed */
  @Override
  public double getQuick(int index) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //return elements[index(index)];
    //manually inlined:
    return elements[offset + offsets[zero + index * stride]];
  }

  /* removed */
  @Override
  protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
    if (other instanceof SelectedDenseDoubleMatrix1D) {
      SelectedDenseDoubleMatrix1D otherMatrix = (SelectedDenseDoubleMatrix1D) other;
      return this.elements == otherMatrix.elements;
    }
    if (other instanceof DenseDoubleMatrix1D) {
      DenseDoubleMatrix1D otherMatrix = (DenseDoubleMatrix1D) other;
      return this.elements == otherMatrix.elements;
    }
    return false;
  }

  /* removed */
  @Override
  protected int index(int rank) {
    //return this.offset + super.index(rank);
    // manually inlined:
    return offset + offsets[zero + rank * stride];
  }

  /* removed */
  @Override
  public DoubleMatrix1D like(int size) {
    return new DenseDoubleMatrix1D(size);
  }

  /* removed */
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return new DenseDoubleMatrix2D(rows, columns);
  }

  /* removed */
  @Override
  public void setQuick(int index, double value) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //elements[index(index)] = value;
    //manually inlined:
    elements[offset + offsets[zero + index * stride]] = value;
  }

  /* removed */
  @Override
  protected void setUp(int size) {
    super.setUp(size);
    this.stride = 1;
    this.offset = 0;
  }

  /* removed */
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    return new SelectedDenseDoubleMatrix1D(this.elements, offsets);
  }
}
