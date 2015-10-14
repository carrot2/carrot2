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

public abstract class AbstractMatrix2D extends AbstractMatrix {

  /* removed */
  protected int columns;
  protected int rows;

  /* removed */
  protected int rowStride;

  /* removed */
  protected int columnStride;

  /* removed */
  protected int rowZero;
  protected int columnZero;

  /* removed */
  protected AbstractMatrix2D() {
  }

  /* removed */
  protected int columnOffset(int absRank) {
    return absRank;
  }

  /* removed */
  protected int columnRank(int rank) {
    return columnZero + rank * columnStride;
    //return columnZero + ((rank+columnFlipMask)^columnFlipMask);
    //return columnZero + rank*columnFlip; // slower
  }

  /* removed */
  protected int rowOffset(int absRank) {
    return absRank;
  }

  /* removed */
  protected int rowRank(int rank) {
    return rowZero + rank * rowStride;
    //return rowZero + ((rank+rowFlipMask)^rowFlipMask);
    //return rowZero + rank*rowFlip; // slower
  }

  /* removed */
  protected void checkBox(int row, int column, int height, int width) {
    if (column < 0 || width < 0 || column + width > columns || row < 0 || height < 0 || row + height > rows) {
      throw new IndexOutOfBoundsException(
          "Column:" + column + ", row:" + row + " ,width:" + width + ", height:" + height);
    }
  }

  /* removed */
  protected void checkColumn(int column) {
    if (column < 0 || column >= columns) {
      throw new IndexOutOfBoundsException("Attempted to access at column=" + column);
    }
  }

  /* removed */
  protected void checkColumnIndexes(int[] indexes) {
    for (int i = indexes.length; --i >= 0;) {
      int index = indexes[i];
      if (index < 0 || index >= columns) {
        checkColumn(index);
      }
    }
  }

  /* removed */
  protected void checkRow(int row) {
    if (row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("Attempted to access at row=" + row);
    }
  }

  /* removed */
  protected void checkRowIndexes(int[] indexes) {
    for (int i = indexes.length; --i >= 0;) {
      int index = indexes[i];
      if (index < 0 || index >= rows) {
        checkRow(index);
      }
    }
  }

  /* removed */
  public void checkShape(AbstractMatrix2D B) {
    if (columns != B.columns || rows != B.rows) {
      throw new IllegalArgumentException("Incompatible dimensions");
    }
  }

  /* removed */
  public int columns() {
    return columns;
  }

  /* removed */
  protected int index(int row, int column) {
    return rowOffset(rowRank(row)) + columnOffset(columnRank(column));
  }

  /* removed */
  public int rows() {
    return rows;
  }

  /* removed */
  protected void setUp(int rows, int columns) {
    setUp(rows, columns, 0, 0, columns, 1);
  }

  /* removed */
  protected void setUp(int rows, int columns, int rowZero, int columnZero, int rowStride, int columnStride) {
    if (rows < 0 || columns < 0) {
      throw new IllegalArgumentException("negative size");
    }
    this.rows = rows;
    this.columns = columns;

    this.rowZero = rowZero;
    this.columnZero = columnZero;

    this.rowStride = rowStride;
    this.columnStride = columnStride;

    this.isNoView = true;
    if ((double) columns * rows > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("matrix too large");
    }
  }

  /* removed */
  @Override
  public int size() {
    return rows * columns;
  }

  /* removed */
  protected AbstractMatrix2D vColumnFlip() {
    if (columns > 0) {
      columnZero += (columns - 1) * columnStride;
      columnStride = -columnStride;
      this.isNoView = false;
    }
    return this;
  }

  /* removed */
  protected AbstractMatrix2D vDice() {
    // swap;
    int tmp = rows;
    rows = columns;
    columns = tmp;
    tmp = rowStride;
    rowStride = columnStride;
    columnStride = tmp;
    tmp = rowZero;
    rowZero = columnZero;
    columnZero = tmp;

    // flips stay unaffected

    this.isNoView = false;
    return this;
  }

  /* removed */
  protected AbstractMatrix2D vPart(int row, int column, int height, int width) {
    checkBox(row, column, height, width);
    this.rowZero += this.rowStride * row;
    this.columnZero += this.columnStride * column;
    this.rows = height;
    this.columns = width;
    this.isNoView = false;
    return this;
  }

  /* removed */
  protected AbstractMatrix2D vRowFlip() {
    if (rows > 0) {
      rowZero += (rows - 1) * rowStride;
      rowStride = -rowStride;
      this.isNoView = false;
    }
    return this;
  }

}
