/* Imported from Mahout. */package org.carrot2.mahout.math.matrix;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.IntIntDoubleFunction;
import org.carrot2.mahout.math.matrix.impl.AbstractMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;

public abstract class DoubleMatrix2D extends AbstractMatrix2D implements Cloneable {

  
  protected DoubleMatrix2D() {
  }

  
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

  
  public void assign(DoubleFunction function) {
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, function.apply(getQuick(row, column)));
      }
    }
  }

  
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

  
  public DoubleMatrix2D assign(DoubleMatrix2D y, DoubleDoubleFunction function) {
    checkShape(y);
    for (int row = rows; --row >= 0;) {
      for (int column = columns; --column >= 0;) {
        setQuick(row, column, function.apply(getQuick(row, column), y.getQuick(row, column)));
      }
    }
    return this;
  }

  
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

  
  public DoubleMatrix2D copy() {
    return like().assign(this);
  }

  
  public boolean equals(double value) {
    return org.carrot2.mahout.math.matrix.linalg.Property.DEFAULT.equals(this, value);
  }

  
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

  
  public double get(int row, int column) {
    if (column < 0 || column >= columns || row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("row:" + row + ", column:" + column);
    }
    return getQuick(row, column);
  }

  
  protected DoubleMatrix2D getContent() {
    return this;
  }

  
  public abstract double getQuick(int row, int column);

  
  protected boolean haveSharedCells(DoubleMatrix2D other) {
    if (other == null) {
      return false;
    }
    if (this == other) {
      return true;
    }
    return getContent().haveSharedCellsRaw(other.getContent());
  }

  
  protected boolean haveSharedCellsRaw(DoubleMatrix2D other) {
    return false;
  }

  
  public DoubleMatrix2D like() {
    return like(rows, columns);
  }

  
  public abstract DoubleMatrix2D like(int rows, int columns);

  
  public abstract DoubleMatrix1D like1D(int size);

  
  protected abstract DoubleMatrix1D like1D(int size, int zero, int stride);

  
  public void set(int row, int column, double value) {
    if (column < 0 || column >= columns || row < 0 || row >= rows) {
      throw new IndexOutOfBoundsException("row:" + row + ", column:" + column);
    }
    setQuick(row, column, value);
  }

  
  public abstract void setQuick(int row, int column, double value);

  
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

  
  protected DoubleMatrix2D view() {
    try {
      return (DoubleMatrix2D) clone();
    } catch (CloneNotSupportedException cnse) {
      throw new IllegalStateException();
    }
  }

  
  public DoubleMatrix1D viewColumn(int column) {
    checkColumn(column);
    int viewSize = this.rows;
    int viewZero = index(0, column);
    int viewStride = this.rowStride;
    return like1D(viewSize, viewZero, viewStride);
  }

  
  public DoubleMatrix2D viewColumnFlip() {
    return (DoubleMatrix2D) view().vColumnFlip();
  }

  
  public DoubleMatrix2D viewDice() {
    return (DoubleMatrix2D) view().vDice();
  }

  
  public DoubleMatrix2D viewPart(int row, int column, int height, int width) {
    return (DoubleMatrix2D) view().vPart(row, column, height, width);
  }

  
  public DoubleMatrix1D viewRow(int row) {
    checkRow(row);
    int viewSize = this.columns;
    int viewZero = index(row, 0);
    int viewStride = this.columnStride;
    return like1D(viewSize, viewZero, viewStride);
  }

  
  public DoubleMatrix2D viewRowFlip() {
    return (DoubleMatrix2D) view().vRowFlip();
  }

  
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

  
  protected abstract DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets);


  
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

  
  public double zSum() {
    if (size() == 0) {
      return 0;
    }
    return aggregate(Functions.PLUS, Functions.IDENTITY);
  }
}
