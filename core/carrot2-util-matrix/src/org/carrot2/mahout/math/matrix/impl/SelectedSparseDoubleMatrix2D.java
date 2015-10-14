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
final class SelectedSparseDoubleMatrix2D extends DoubleMatrix2D {
  /*
   * The elements of the matrix.
   */
  final AbstractIntDoubleMap elements;

  /* removed */
  int[] rowOffsets;
  int[] columnOffsets;

  /* removed */
  int offset;

  /* removed */
  SelectedSparseDoubleMatrix2D(int rows, int columns, AbstractIntDoubleMap elements, int rowZero,
                                         int columnZero, int rowStride, int columnStride, int[] rowOffsets,
                                         int[] columnOffsets, int offset) {
    // be sure parameters are valid, we do not check...
    setUp(rows, columns, rowZero, columnZero, rowStride, columnStride);

    this.elements = elements;
    this.rowOffsets = rowOffsets;
    this.columnOffsets = columnOffsets;
    this.offset = offset;

    this.isNoView = false;
  }

  /* removed */
  SelectedSparseDoubleMatrix2D(AbstractIntDoubleMap elements, int[] rowOffsets, int[] columnOffsets,
                                         int offset) {
    this(rowOffsets.length, columnOffsets.length, elements, 0, 0, 1, 1, rowOffsets, columnOffsets, offset);
  }

  /* removed */
  @Override
  protected int columnOffset(int absRank) {
    return columnOffsets[absRank];
  }

  /* removed */
  @Override
  protected int rowOffset(int absRank) {
    return rowOffsets[absRank];
  }

  /* removed */
  @Override
  public double getQuick(int row, int column) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //return elements.get(index(row,column));
    //manually inlined:
    return elements
        .get(offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride]);
  }

  /* removed */
  @Override
  protected boolean haveSharedCellsRaw(DoubleMatrix2D other) {
    if (other instanceof SelectedSparseDoubleMatrix2D) {
      SelectedSparseDoubleMatrix2D otherMatrix = (SelectedSparseDoubleMatrix2D) other;
      return this.elements == otherMatrix.elements;
    }
    if (other instanceof SparseDoubleMatrix2D) {
      SparseDoubleMatrix2D otherMatrix = (SparseDoubleMatrix2D) other;
      return this.elements == otherMatrix.elements;
    }
    return false;
  }

  /* removed */
  @Override
  protected int index(int row, int column) {
    //return this.offset + super.index(row,column);
    //manually inlined:
    return this.offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride];
  }

  /* removed */
  @Override
  public DoubleMatrix2D like(int rows, int columns) {
    return new SparseDoubleMatrix2D(rows, columns);
  }

  /* removed */
  @Override
  public DoubleMatrix1D like1D(int size) {
    return new SparseDoubleMatrix1D(size);
  }

  /* removed */
  @Override
  protected DoubleMatrix1D like1D(int size, int zero, int stride) {
    throw new UnsupportedOperationException();
    // this method is never called since viewRow() and viewColumn are overridden properly.
  }

  /* removed */
  @Override
  public void setQuick(int row, int column, double value) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //int index =  index(row,column);
    //manually inlined:
    int index = offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride];

    if (value == 0) {
      this.elements.removeKey(index);
    } else {
      this.elements.put(index, value);
    }
  }

  /* removed */
  @Override
  protected void setUp(int rows, int columns) {
    super.setUp(rows, columns);
    this.rowStride = 1;
    this.columnStride = 1;
    this.offset = 0;
  }

  /* removed */
  @Override
  protected AbstractMatrix2D vDice() {
    super.vDice();
    // swap
    int[] tmp = rowOffsets;
    rowOffsets = columnOffsets;
    columnOffsets = tmp;

    // flips stay unaffected

    this.isNoView = false;
    return this;
  }

  /* removed */
  @Override
  public DoubleMatrix1D viewColumn(int column) {
    checkColumn(column);
    int viewSize = this.rows;
    int viewZero = this.rowZero;
    int viewStride = this.rowStride;
    int[] viewOffsets = this.rowOffsets;
    int viewOffset = this.offset + columnOffset(columnRank(column));
    return new SelectedSparseDoubleMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
  }

  /* removed */
  @Override
  public DoubleMatrix1D viewRow(int row) {
    checkRow(row);
    int viewSize = this.columns;
    int viewZero = columnZero;
    int viewStride = this.columnStride;
    int[] viewOffsets = this.columnOffsets;
    int viewOffset = this.offset + rowOffset(rowRank(row));
    return new SelectedSparseDoubleMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
  }

  /* removed */
  @Override
  protected DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets) {
    return new SelectedSparseDoubleMatrix2D(this.elements, rowOffsets, columnOffsets, this.offset);
  }
}
