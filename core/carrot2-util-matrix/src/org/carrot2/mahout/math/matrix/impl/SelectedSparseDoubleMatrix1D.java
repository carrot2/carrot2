/*
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.map.AbstractIntDoubleMap;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

/* removed */
final class SelectedSparseDoubleMatrix1D extends DoubleMatrix1D {
  /*
   * The elements of the matrix.
   */
  final AbstractIntDoubleMap elements;

  /* removed */
  private final int[] offsets;

  /* removed */
  private int offset;

  /* removed */
  SelectedSparseDoubleMatrix1D(int size, AbstractIntDoubleMap elements, int zero, int stride, int[] offsets,
                                         int offset) {
    setUp(size, zero, stride);

    this.elements = elements;
    this.offsets = offsets;
    this.offset = offset;
    this.isNoView = false;
  }

  /* removed */
  SelectedSparseDoubleMatrix1D(AbstractIntDoubleMap elements, int[] offsets) {
    this(offsets.length, elements, 0, 1, offsets, 0);
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
    //return elements.get(index(index));
    //manually inlined:
    return elements.get(offset + offsets[zero + index * stride]);
  }

  /* removed */
  @Override
  protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
    if (other instanceof SelectedSparseDoubleMatrix1D) {
      SelectedSparseDoubleMatrix1D otherMatrix = (SelectedSparseDoubleMatrix1D) other;
      return this.elements == otherMatrix.elements;
    }
    if (other instanceof SparseDoubleMatrix1D) {
      SparseDoubleMatrix1D otherMatrix = (SparseDoubleMatrix1D) other;
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
    return new SparseDoubleMatrix1D(size);
  }

  /* removed */
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return new SparseDoubleMatrix2D(rows, columns);
  }

  /* removed */
  @Override
  public void setQuick(int index, double value) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //int i =  index(index);
    //manually inlined:
    int i = offset + offsets[zero + index * stride];
    if (value == 0) {
      this.elements.removeKey(i);
    } else {
      this.elements.put(i, value);
    }
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
    return new SelectedSparseDoubleMatrix1D(this.elements, offsets);
  }
}
