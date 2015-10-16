/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;


final class SelectedDenseDoubleMatrix2D extends DoubleMatrix2D {

  
  final double[] elements;

  
  private int[] rowOffsets;
  private int[] columnOffsets;

  
  private int offset;

  
  SelectedDenseDoubleMatrix2D(double[] elements, int[] rowOffsets, int[] columnOffsets, int offset) {
    this(rowOffsets.length, columnOffsets.length, elements, 0, 0, 1, 1, rowOffsets, columnOffsets, offset);
  }

  
  SelectedDenseDoubleMatrix2D(int rows, int columns, double[] elements, int rowZero, int columnZero,
                                        int rowStride, int columnStride, int[] rowOffsets, int[] columnOffsets,
                                        int offset) {
    // be sure parameters are valid, we do not check...
    setUp(rows, columns, rowZero, columnZero, rowStride, columnStride);

    this.elements = elements;
    this.rowOffsets = rowOffsets;
    this.columnOffsets = columnOffsets;
    this.offset = offset;

    this.isNoView = false;
  }

  
  @Override
  protected int columnOffset(int absRank) {
    return columnOffsets[absRank];
  }

  
  @Override
  protected int rowOffset(int absRank) {
    return rowOffsets[absRank];
  }

  
  @Override
  public double getQuick(int row, int column) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //return elements[index(row,column)];
    //manually inlined:
    return elements[offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride]];
  }

  
  @Override
  protected boolean haveSharedCellsRaw(DoubleMatrix2D other) {
    if (other instanceof SelectedDenseDoubleMatrix2D) {
      SelectedDenseDoubleMatrix2D otherMatrix = (SelectedDenseDoubleMatrix2D) other;
      return this.elements == otherMatrix.elements;
    }
    if (other instanceof DenseDoubleMatrix2D) {
      DenseDoubleMatrix2D otherMatrix = (DenseDoubleMatrix2D) other;
      return this.elements == otherMatrix.elements;
    }
    return false;
  }

  
  @Override
  protected int index(int row, int column) {
    //return this.offset + super.index(row,column);
    //manually inlined:
    return this.offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride];
  }

  
  @Override
  public DoubleMatrix2D like(int rows, int columns) {
    return new DenseDoubleMatrix2D(rows, columns);
  }

  
  @Override
  public DoubleMatrix1D like1D(int size) {
    return new DenseDoubleMatrix1D(size);
  }

  
  @Override
  protected DoubleMatrix1D like1D(int size, int zero, int stride) {
    throw new UnsupportedOperationException();
    // this method is never called since viewRow() and viewColumn are overridden properly.
  }

  
  @Override
  public void setQuick(int row, int column, double value) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //elements[index(row,column)] = value;
    //manually inlined:
    elements[offset + rowOffsets[rowZero + row * rowStride] + columnOffsets[columnZero + column * columnStride]] =
        value;
  }

  
  @Override
  protected void setUp(int rows, int columns) {
    super.setUp(rows, columns);
    this.rowStride = 1;
    this.columnStride = 1;
    this.offset = 0;
  }

  
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

  
  @Override
  public DoubleMatrix1D viewColumn(int column) {
    checkColumn(column);
    int viewSize = this.rows;
    int viewZero = this.rowZero;
    int viewStride = this.rowStride;
    int[] viewOffsets = this.rowOffsets;
    int viewOffset = this.offset + columnOffset(columnRank(column));
    return new SelectedDenseDoubleMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
  }

  
  @Override
  public DoubleMatrix1D viewRow(int row) {
    checkRow(row);
    int viewSize = this.columns;
    int viewZero = columnZero;
    int viewStride = this.columnStride;
    int[] viewOffsets = this.columnOffsets;
    int viewOffset = this.offset + rowOffset(rowRank(row));
    return new SelectedDenseDoubleMatrix1D(viewSize, this.elements, viewZero, viewStride, viewOffsets, viewOffset);
  }

  
  @Override
  protected DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets) {
    return new SelectedDenseDoubleMatrix2D(this.elements, rowOffsets, columnOffsets, this.offset);
  }
}
