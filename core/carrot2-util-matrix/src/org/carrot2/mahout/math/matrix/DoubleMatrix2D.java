/*
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.matrix;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.IntIntDoubleFunction;
import org.carrot2.mahout.math.matrix.impl.AbstractMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;

public abstract class DoubleMatrix2D extends AbstractMatrix2D implements Cloneable {

  /* removed */
  protected DoubleMatrix2D() {
  }

  /* removed */
  public double aggregate(DoubleDoubleFunction aggr,
                          DoubleFunction f) {
    if (size() == 0) {
      return Double.NaN;
    }
    double a = f.apply(getQuick(rows - 1, columns - 1));
    int d = 1; // last cell already done
    for (int row = rows; --row >= 0;) {
      for (int column = columns - d; --column >= 0;) {
        a = aggr.apply(a, f.apply(getQuick(row, column)));
      }
      d = 0;
    }
    return a;
  }

  /* removed */
  public double aggregate(DoubleMatrix2D other, DoubleDoubleFunction aggr,
                          DoubleDoubleFunction f) {
    checkShape(other);
    if (size() == 0) {
      return Double.NaN;
    }
    double a = f.apply(getQuick(rows - 1, columns - 1), other.getQuick(rows - 1, columns - 1));
    int d = 1; // last cell already done
    for (int row = rows; --row >= 0;) {
      for (int column = columns - d; --column >= 0;) {
        a = aggr.apply(a, f.apply(getQuick(row, column), other.getQuick(row, column)));
      }
      d = 0;
    }
    return a;
  }

  /* removed */
  public void assign(double[][] values) {
    if (values.length != rows) {
      throw new IllegalArgumentException("Must have same number of rows: rows=" + values.length + "rows()=" + rows());
    }
    for (int row = rows; --row >= 0;) {
      double[] currentRow = values[row];
      if (currentRow.length != columns) {
        throw new IllegalArgumentException(
            "Must have same number of columns in every row: columns=" + currentRow.length + "columns()=" + columns());
      }
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, currentRow[column]);
      }
    }
  }

  /* removed */
  public DoubleMatrix2D assign(double value) {
    int r = rows;
    int c = columns;
    //for (int row=rows; --row >= 0;) {
    //  for (int column=columns; --column >= 0;) {
    for (int row = 0; row < r; row++) {
      for (int column = 0; column < c; column++) {
        setQuick(row, column, value);
      }
    }
    return this;
  }

  /* removed */
  public void assign(DoubleFunction function) {
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, function.apply(getQuick(row, column)));
      }
    }
  }

  /* removed */
  public DoubleMatrix2D assign(DoubleMatrix2D other) {
    if (other == this) {
      return this;
    }
    checkShape(other);
    if (haveSharedCells(other)) {
      other = other.copy();
    }

    //for (int row=0; row<rows; row++) {
    //for (int column=0; column<columns; column++) {
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, other.getQuick(row, column));
      }
    }
    return this;
  }

  /* removed */
  public DoubleMatrix2D assign(DoubleMatrix2D y, DoubleDoubleFunction function) {
    checkShape(y);
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, function.apply(getQuick(row, column), y.getQuick(row, column)));
      }
    }
    return this;
  }

  /* removed */
  public int cardinality() {
    int cardinality = 0;
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        if (getQuick(row, column) != 0) {
          cardinality++;
        }
      }
    }
    return cardinality;
  }

  /* removed */
  public DoubleMatrix2D copy() {
    return like().assign(this);
  }

  /* removed */
  public boolean equals(double value) {
    return org.carrot2.mahout.math.matrix.linalg.Property.DEFAULT.equals(this, value);
  }

  /* removed */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DoubleMatrix2D)) {
      return false;
    }

    return org.carrot2.mahout.math.matrix.linalg.Property.DEFAULT.equals(this, (DoubleMatrix2D) obj);
  }

  /* removed */
  public void forEachNonZero(IntIntDoubleFunction function) {
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        double value = getQuick(row, column);
        if (value != 0) {
          double r = function.apply(row, column, value);
          if (r != value) {
            setQuick(row, column, r);
          }
        }
      }
    }
  }

  /* removed */
  public double get(int row, int column) {
    if (column < 0 || column >= columns || row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("row:" + row + ", column:" + column);
    }
    return getQuick(row, column);
  }

  /* removed */
  protected DoubleMatrix2D getContent() {
    return this;
  }

  /* removed */
  public abstract double getQuick(int row, int column);

  /* removed */
  protected boolean haveSharedCells(DoubleMatrix2D other) {
    if (other == null) {
      return false;
    }
    if (this == other) {
      return true;
    }
    return getContent().haveSharedCellsRaw(other.getContent());
  }

  /* removed */
  protected boolean haveSharedCellsRaw(DoubleMatrix2D other) {
    return false;
  }

  /* removed */
  public DoubleMatrix2D like() {
    return like(rows, columns);
  }

  /* removed */
  public abstract DoubleMatrix2D like(int rows, int columns);

  /* removed */
  public abstract DoubleMatrix1D like1D(int size);

  /* removed */
  protected abstract DoubleMatrix1D like1D(int size, int zero, int stride);

  /* removed */
  public void set(int row, int column, double value) {
    if (column < 0 || column >= columns || row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("row:" + row + ", column:" + column);
    }
    setQuick(row, column, value);
  }

  /* removed */
  public abstract void setQuick(int row, int column, double value);

  /* removed */
  public double[][] toArray() {
    double[][] values = new double[rows][columns];
    for (int row = rows; --row >= 0;) {
      double[] currentRow = values[row];
      for (int column = columns; --column >= 0;) {
        currentRow[column] = getQuick(row, column);
      }
    }
    return values;
  }

  /* removed */
  protected DoubleMatrix2D view() {
    try {
      return (DoubleMatrix2D) clone();
    } catch (CloneNotSupportedException cnse) {
      throw new IllegalStateException();
    }
  }

  /* removed */
  public DoubleMatrix1D viewColumn(int column) {
    checkColumn(column);
    int viewSize = this.rows;
    int viewZero = index(0, column);
    int viewStride = this.rowStride;
    return like1D(viewSize, viewZero, viewStride);
  }

  /* removed */
  public DoubleMatrix2D viewColumnFlip() {
    return (DoubleMatrix2D) view().vColumnFlip();
  }

  /* removed */
  public DoubleMatrix2D viewDice() {
    return (DoubleMatrix2D) view().vDice();
  }

  /* removed */
  public DoubleMatrix2D viewPart(int row, int column, int height, int width) {
    return (DoubleMatrix2D) view().vPart(row, column, height, width);
  }

  /* removed */
  public DoubleMatrix1D viewRow(int row) {
    checkRow(row);
    int viewSize = this.columns;
    int viewZero = index(row, 0);
    int viewStride = this.columnStride;
    return like1D(viewSize, viewZero, viewStride);
  }

  /* removed */
  public DoubleMatrix2D viewRowFlip() {
    return (DoubleMatrix2D) view().vRowFlip();
  }

  /* removed */
  public DoubleMatrix2D viewSelection(int[] rowIndexes, int[] columnIndexes) {
    // check for "all"
    if (rowIndexes == null) {
      rowIndexes = new int[rows];
      for (int i = rows; --i >= 0;) {
        rowIndexes[i] = i;
      }
    }
    if (columnIndexes == null) {
      columnIndexes = new int[columns];
      for (int i = columns; --i >= 0;) {
        columnIndexes[i] = i;
      }
    }

    checkRowIndexes(rowIndexes);
    checkColumnIndexes(columnIndexes);
    int[] rowOffsets = new int[rowIndexes.length];
    int[] columnOffsets = new int[columnIndexes.length];
    for (int i = rowIndexes.length; --i >= 0;) {
      rowOffsets[i] = rowOffset(rowRank(rowIndexes[i]));
    }
    for (int i = columnIndexes.length; --i >= 0;) {
      columnOffsets[i] = columnOffset(columnRank(columnIndexes[i]));
    }
    return viewSelectionLike(rowOffsets, columnOffsets);
  }

  /* removed */
  /*
  public DoubleMatrix2D viewSelection(DoubleMatrix1DProcedure condition) {
    IntArrayList matches = new IntArrayList();
    for (int i = 0; i < rows; i++) {
      if (condition.apply(viewRow(i))) {
        matches.add(i);
      }
    }

    matches.trimToSize();
    return viewSelection(matches.elements(), null); // take all columns
  }
   */

  /* removed */
  protected abstract DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets);


  /* removed */
  public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha, double beta, boolean transposeA) {
    if (transposeA) {
      return viewDice().zMult(y, z, alpha, beta, false);
    }
    //boolean ignore = (z==null);
    if (z == null) {
      z = new DenseDoubleMatrix1D(this.rows);
    }
    if (columns != y.size() || rows > z.size()) {
      throw new IllegalArgumentException("Incompatible args");
    }

    for (int i = rows; --i >= 0;) {
      double s = 0;
      for (int j = columns; --j >= 0;) {
        s += getQuick(i, j) * y.getQuick(j);
      }
      z.setQuick(i, alpha * s + beta * z.getQuick(i));
    }
    return z;
  }

  /* removed */
  public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C, double alpha, double beta, boolean transposeA,
                              boolean transposeB) {
    if (transposeA) {
      return viewDice().zMult(B, C, alpha, beta, false, transposeB);
    }
    if (transposeB) {
      return this.zMult(B.viewDice(), C, alpha, beta, transposeA, false);
    }

    int m = rows;
    int n = columns;
    int p = B.columns;

    if (C == null) {
      C = new DenseDoubleMatrix2D(m, p);
    }
    if (B.rows != n) {
      throw new IllegalArgumentException("Matrix2D inner dimensions must agree");
    }
    if (C.rows != m || C.columns != p) {
      throw new IllegalArgumentException("Incompatible result matrix");
    }
    if (this == C || B == C) {
      throw new IllegalArgumentException("Matrices must not be identical");
    }

    for (int j = p; --j >= 0;) {
      for (int i = m; --i >= 0;) {
        double s = 0;
        for (int k = n; --k >= 0;) {
          s += getQuick(i, k) * B.getQuick(k, j);
        }
        C.setQuick(i, j, alpha * s + beta * C.getQuick(i, j));
      }
    }
    return C;
  }

  /* removed */
  public double zSum() {
    if (size() == 0) {
      return 0;
    }
    return aggregate(Functions.PLUS, Functions.IDENTITY);
  }
}
