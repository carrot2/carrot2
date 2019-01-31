/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.Mult;
import org.carrot2.mahout.math.function.PlusMult;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

public final class DenseDoubleMatrix2D extends DoubleMatrix2D {

  
  final double[] elements;

  
  public DenseDoubleMatrix2D(double[][] values) {
    this(values.length, values.length == 0 ? 0 : values[0].length);
    assign(values);
  }

  
  public DenseDoubleMatrix2D(int rows, int columns) {
    setUp(rows, columns);
    this.elements = new double[rows * columns];
  }

  
  public static DoubleMatrix2D identity(int rowsAndColumns) {
    DoubleMatrix2D matrix = new DenseDoubleMatrix2D(rowsAndColumns, rowsAndColumns);
    for (int i = rowsAndColumns; --i >= 0;) {
      matrix.setQuick(i, i, 1);
    }
    return matrix;
  }

  
  @Override
  public void assign(double[][] values) {
    if (this.isNoView) {
      if (values.length != rows) {
        throw new IllegalArgumentException("Must have same number of rows: rows=" + values.length + "rows()=" + rows());
      }
      int i = columns * (rows - 1);
      for (int row = rows; --row >= 0;) {
        double[] currentRow = values[row];
        if (currentRow.length != columns) {
          throw new IllegalArgumentException(
              "Must have same number of columns in every row: columns=" + currentRow.length + "columns()=" + columns());
        }
        System.arraycopy(currentRow, 0, this.elements, i, columns);
        i -= columns;
      }
    } else {
      super.assign(values);
    }
  }

  
  @Override
  public DoubleMatrix2D assign(double value) {
    double[] elems = this.elements;
    int index = index(0, 0);
    int cs = this.columnStride;
    int rs = this.rowStride;
    for (int row = rows; --row >= 0;) {
      for (int i = index, column = columns; --column >= 0;) {
        elems[i] = value;
        i += cs;
      }
      index += rs;
    }
    return this;
  }

  
  @Override
  public void assign(DoubleFunction function) {
    double[] elems = this.elements;
    if (elems == null) {
      throw new IllegalStateException();
    }
    int index = index(0, 0);
    int cs = this.columnStride;
    int rs = this.rowStride;

    // specialization for speed
    if (function instanceof Mult) { // x[i] = mult*x[i]
      double multiplicator = ((Mult) function).getMultiplicator();
      if (multiplicator == 1) {
        return;
      }
      if (multiplicator == 0) {
        assign(0);
        return;
      }
      for (int row = rows; --row >= 0;) { // the general case
        for (int i = index, column = columns; --column >= 0;) {
          elems[i] *= multiplicator;
          i += cs;
        }
        index += rs;
      }
    } else { // the general case x[i] = f(x[i])
      for (int row = rows; --row >= 0;) {
        for (int i = index, column = columns; --column >= 0;) {
          elems[i] = function.apply(elems[i]);
          i += cs;
        }
        index += rs;
      }
    }
  }

  
  @Override
  public DoubleMatrix2D assign(DoubleMatrix2D source) {
    // overriden for performance only
    if (!(source instanceof DenseDoubleMatrix2D)) {
      return super.assign(source);
    }
    DenseDoubleMatrix2D other = (DenseDoubleMatrix2D) source;
    if (other == this) {
      return this;
    } // nothing to do
    checkShape(other);

    if (this.isNoView && other.isNoView) { // quickest
      System.arraycopy(other.elements, 0, this.elements, 0, this.elements.length);
      return this;
    }

    if (haveSharedCells(other)) {
      DoubleMatrix2D c = other.copy();
      if (!(c instanceof DenseDoubleMatrix2D)) { // should not happen
        return super.assign(other);
      }
      other = (DenseDoubleMatrix2D) c;
    }

    double[] elems = this.elements;
    double[] otherElems = other.elements;
    if (elems == null || otherElems == null) {
      throw new IllegalStateException();
    }
    int cs = this.columnStride;
    int ocs = other.columnStride;
    int rs = this.rowStride;
    int ors = other.rowStride;

    int otherIndex = other.index(0, 0);
    int index = index(0, 0);
    for (int row = rows; --row >= 0;) {
      for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
        elems[i] = otherElems[j];
        i += cs;
        j += ocs;
      }
      index += rs;
      otherIndex += ors;
    }
    return this;
  }

  
  @Override
  public DoubleMatrix2D assign(DoubleMatrix2D y, DoubleDoubleFunction function) {
    // overriden for performance only
    if (!(y instanceof DenseDoubleMatrix2D)) {
      return super.assign(y, function);
    }
    DenseDoubleMatrix2D other = (DenseDoubleMatrix2D) y;
    checkShape(y);

    double[] elems = this.elements;
    double[] otherElems = other.elements;
    if (elems == null || otherElems == null) {
      throw new IllegalStateException();
    }
    int cs = this.columnStride;
    int ocs = other.columnStride;
    int rs = this.rowStride;
    int ors = other.rowStride;

    int otherIndex = other.index(0, 0);
    int index = index(0, 0);

    // specialized for speed
    if (function == Functions.MULT) { // x[i] = x[i] * y[i]
      for (int row = rows; --row >= 0;) {
        for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
          elems[i] *= otherElems[j];
          i += cs;
          j += ocs;
        }
        index += rs;
        otherIndex += ors;
      }
    } else if (function == Functions.DIV) { // x[i] = x[i] / y[i]
      for (int row = rows; --row >= 0;) {
        for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
          elems[i] /= otherElems[j];
          i += cs;
          j += ocs;
        }
        index += rs;
        otherIndex += ors;
      }
    } else if (function instanceof PlusMult) {
      double multiplicator = ((PlusMult) function).getMultiplicator();
      if (multiplicator == 0) { // x[i] = x[i] + 0*y[i]
        return this;
      } else if (multiplicator == 1) { // x[i] = x[i] + y[i]
        for (int row = rows; --row >= 0;) {
          for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
            elems[i] += otherElems[j];
            i += cs;
            j += ocs;
          }
          index += rs;
          otherIndex += ors;
        }
      } else if (multiplicator == -1) { // x[i] = x[i] - y[i]
        for (int row = rows; --row >= 0;) {
          for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
            elems[i] -= otherElems[j];
            i += cs;
            j += ocs;
          }
          index += rs;
          otherIndex += ors;
        }
      } else { // the general case
        for (int row = rows; --row >= 0;) { // x[i] = x[i] + mult*y[i]
          for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
            elems[i] += multiplicator * otherElems[j];
            i += cs;
            j += ocs;
          }
          index += rs;
          otherIndex += ors;
        }
      }
    } else { // the general case x[i] = f(x[i],y[i])
      for (int row = rows; --row >= 0;) {
        for (int i = index, j = otherIndex, column = columns; --column >= 0;) {
          elems[i] = function.apply(elems[i], otherElems[j]);
          i += cs;
          j += ocs;
        }
        index += rs;
        otherIndex += ors;
      }
    }
    return this;
  }

  
  @Override
  public double getQuick(int row, int column) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //return elements[index(row,column)];
    //manually inlined:
    return elements[rowZero + row * rowStride + columnZero + column * columnStride];
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
    // return super.index(row,column);
    // manually inlined for speed:
    return rowZero + row * rowStride + columnZero + column * columnStride;
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
    return new DenseDoubleMatrix1D(size, this.elements, zero, stride);
  }

  
  @Override
  public void setQuick(int row, int column, double value) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //elements[index(row,column)] = value;
    //manually inlined:
    elements[rowZero + row * rowStride + columnZero + column * columnStride] = value;
  }

  
  @Override
  protected DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets) {
    return new SelectedDenseDoubleMatrix2D(this.elements, rowOffsets, columnOffsets, 0);
  }

  @Override
  public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha, double beta, boolean transposeA) {
    if (transposeA) {
      return viewDice().zMult(y, z, alpha, beta, false);
    }
    if (z == null) {
      z = new DenseDoubleMatrix1D(this.rows);
    }
    if (!(y instanceof DenseDoubleMatrix1D && z instanceof DenseDoubleMatrix1D)) {
      return super.zMult(y, z, alpha, beta, transposeA);
    }

    if (columns != y.size || rows > z.size) {
      throw new IllegalArgumentException("Incompatible sizes");
    }

    DenseDoubleMatrix1D yy = (DenseDoubleMatrix1D) y;
    DenseDoubleMatrix1D zz = (DenseDoubleMatrix1D) z;
    double[] AElems = this.elements;
    double[] yElems = yy.elements;
    double[] zElems = zz.elements;
    if (AElems == null || yElems == null || zElems == null) {
      throw new IllegalStateException();
    }
    int As = this.columnStride;
    int ys = yy.stride;
    int zs = zz.stride;

    int indexA = index(0, 0);
    int indexY = yy.index(0);
    int indexZ = zz.index(0);

    int cols = columns;
    for (int row = rows; --row >= 0;) {
      double sum = 0;
      // loop unrolled
      int i = indexA - As;
      int j = indexY - ys;
      for (int k = cols % 4; --k >= 0;) {
        sum += AElems[i += As] * yElems[j += ys];
      }
      for (int k = cols / 4; --k >= 0;) {
        sum += AElems[i += As] * yElems[j += ys]
            + AElems[i += As] * yElems[j += ys] 
            + AElems[i += As] * yElems[j += ys]
            + AElems[i += As] * yElems[j += ys];
      }

      zElems[indexZ] = alpha * sum + beta * zElems[indexZ];
      indexA += this.rowStride;
      indexZ += zs;
    }

    return z;
  }

  @Override
  public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C, double alpha, double beta, boolean transposeA,
                              boolean transposeB) {
    // overriden for performance only
    if (transposeA) {
      return viewDice().zMult(B, C, alpha, beta, false, transposeB);
    }
    if (B instanceof SparseDoubleMatrix2D) {
      // exploit quick sparse mult
      // A*B = (B' * A')'
      if (C == null) {
        return B.zMult(this, null, alpha, beta, !transposeB, true).viewDice();
      } else {
        B.zMult(this, C.viewDice(), alpha, beta, !transposeB, true);
        return C;
      }
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
    if (!(C instanceof DenseDoubleMatrix2D)) {
      return super.zMult(B, C, alpha, beta, transposeA, transposeB);
    }
    if (B.rows != n) {
      throw new IllegalArgumentException(
          "Matrix2D inner dimensions must agree");
    }
    if (C.rows != m || C.columns != p) {
      throw new IllegalArgumentException(
          "Incompatible result matrix");
    }
    if (this == C || B == C) {
      throw new IllegalArgumentException("Matrices must not be identical");
    }

    DenseDoubleMatrix2D BB = (DenseDoubleMatrix2D) B;
    DenseDoubleMatrix2D CC = (DenseDoubleMatrix2D) C;
    double[] AElems = this.elements;
    double[] BElems = BB.elements;
    double[] CElems = CC.elements;
    if (AElems == null || BElems == null || CElems == null) {
      throw new IllegalStateException();
    }

    int cA = this.columnStride;
    int cB = BB.columnStride;
    int cC = CC.columnStride;

    int rA = this.rowStride;
    int rB = BB.rowStride;
    int rC = CC.rowStride;

    /*
    A is blocked to hide memory latency
        xxxxxxx B
        xxxxxxx
        xxxxxxx
    A
    xxx     xxxxxxx C
    xxx     xxxxxxx
    ---     -------
    xxx     xxxxxxx
    xxx     xxxxxxx
    ---     -------
    xxx     xxxxxxx
    */
    int blockSize = 30000; // * 8 == Level 2 cache in bytes
    //if (n+p == 0) return C;
    //int m_optimal = (BLOCK_SIZE - n*p) / (n+p);
    int mOptimal = (blockSize - n) / (n + 1);
    if (mOptimal <= 0) {
      mOptimal = 1;
    }
    int blocks = m / mOptimal;
    if (m % mOptimal != 0) {
      blocks++;
    }
    int rr = 0;
    while (--blocks >= 0) {
      int jB = BB.index(0, 0);
      int indexA = index(rr, 0);
      int jC = CC.index(rr, 0);
      rr += mOptimal;
      if (blocks == 0) {
        mOptimal += m - rr;
      }

      for (int j = p; --j >= 0;) {
        int iA = indexA;
        int iC = jC;
        for (int i = mOptimal; --i >= 0;) {
          int kA = iA;
          int kB = jB;

          /*
          // not unrolled:
          for (int k = n; --k >= 0; ) {
            //s += getQuick(i,k) * B.getQuick(k,j);
            s += AElems[kA] * BElems[kB];
            kB += rB;
            kA += cA;
          }
          */

          // loop unrolled
          kA -= cA;
          kB -= rB;

          double s = 0;
          for (int k = n % 4; --k >= 0;) {
            s += AElems[kA += cA] * BElems[kB += rB];
          }
          for (int k = n / 4; --k >= 0;) {
            s += AElems[kA += cA] * BElems[kB += rB]
                + AElems[kA += cA] * BElems[kB += rB] 
                + AElems[kA += cA] * BElems[kB += rB]
                + AElems[kA += cA] * BElems[kB += rB];
          }

          CElems[iC] = alpha * s + beta * CElems[iC];
          iA += rA;
          iC += rC;
        }
        jB += cB;
        jC += cC;
      }
    }
    return C;
  }

  
  @Override
  public double zSum() {
    double[] elems = this.elements;
    if (elems == null) {
      throw new IllegalStateException();
    }
    int index = index(0, 0);
    int cs = this.columnStride;
    int rs = this.rowStride;
    double sum = 0;
    for (int row = rows; --row >= 0;) {
      for (int i = index, column = columns; --column >= 0;) {
        sum += elems[i];
        i += cs;
      }
      index += rs;
    }
    return sum;
  }
}
