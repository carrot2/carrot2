/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;



public abstract class AbstractMatrix2D extends AbstractMatrix {

  
  protected int columns;
  protected int rows;

  
  protected int rowStride;

  
  protected int columnStride;

  
  protected int rowZero;
  protected int columnZero;

  
  protected AbstractMatrix2D() {
  }

  
  protected int columnOffset(int absRank) {
    return absRank;
  }

  
  protected int columnRank(int rank) {
    return columnZero + rank * columnStride;
    //return columnZero + ((rank+columnFlipMask)^columnFlipMask);
    //return columnZero + rank*columnFlip; // slower
  }

  
  protected int rowOffset(int absRank) {
    return absRank;
  }

  
  protected int rowRank(int rank) {
    return rowZero + rank * rowStride;
    //return rowZero + ((rank+rowFlipMask)^rowFlipMask);
    //return rowZero + rank*rowFlip; // slower
  }

  
  protected void checkBox(int row, int column, int height, int width) {
    if (column < 0 || width < 0 || column + width > columns || row < 0 || height < 0 || row + height > rows) {
      throw new IndexOutOfBoundsException(
          "Column:" + column + ", row:" + row + " ,width:" + width + ", height:" + height);
    }
  }

  
  protected void checkColumn(int column) {
    if (column < 0 || column >= columns) {
      throw new IndexOutOfBoundsException("Attempted to access at column=" + column);
    }
  }

  
  protected void checkColumnIndexes(int[] indexes) {
    for (int i = indexes.length; --i >= 0;) {
      int index = indexes[i];
      if (index < 0 || index >= columns) {
        checkColumn(index);
      }
    }
  }

  
  protected void checkRow(int row) {
    if (row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("Attempted to access at row=" + row);
    }
  }

  
  protected void checkRowIndexes(int[] indexes) {
    for (int i = indexes.length; --i >= 0;) {
      int index = indexes[i];
      if (index < 0 || index >= rows) {
        checkRow(index);
      }
    }
  }

  
  public void checkShape(AbstractMatrix2D B) {
    if (columns != B.columns || rows != B.rows) {
      throw new IllegalArgumentException("Incompatible dimensions");
    }
  }

  
  public int columns() {
    return columns;
  }

  
  protected int index(int row, int column) {
    return rowOffset(rowRank(row)) + columnOffset(columnRank(column));
  }

  
  public int rows() {
    return rows;
  }

  
  protected void setUp(int rows, int columns) {
    setUp(rows, columns, 0, 0, columns, 1);
  }

  
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

  
  @Override
  public int size() {
    return rows * columns;
  }

  
  protected AbstractMatrix2D vColumnFlip() {
    if (columns > 0) {
      columnZero += (columns - 1) * columnStride;
      columnStride = -columnStride;
      this.isNoView = false;
    }
    return this;
  }

  
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

  
  protected AbstractMatrix2D vPart(int row, int column, int height, int width) {
    checkBox(row, column, height, width);
    this.rowZero += this.rowStride * row;
    this.columnZero += this.columnStride * column;
    this.rows = height;
    this.columns = width;
    this.isNoView = false;
    return this;
  }

  
  protected AbstractMatrix2D vRowFlip() {
    if (rows > 0) {
      rowZero += (rows - 1) * rowStride;
      rowStride = -rowStride;
      this.isNoView = false;
    }
    return this;
  }

}
