/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.IntDoubleProcedure;
import org.carrot2.mahout.math.function.IntIntDoubleFunction;
import org.carrot2.mahout.math.function.Mult;
import org.carrot2.mahout.math.function.PlusMult;
import org.carrot2.mahout.math.map.AbstractIntDoubleMap;
import org.carrot2.mahout.math.map.OpenIntDoubleHashMap;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

public final class SparseDoubleMatrix2D extends DoubleMatrix2D {
  /*
   * The elements of the matrix.
   */
  final AbstractIntDoubleMap elements;

  
  public SparseDoubleMatrix2D(double[][] values) {
    this(values.length, values.length == 0 ? 0 : values[0].length);
    assign(values);
  }

  
  public SparseDoubleMatrix2D(int rows, int columns) {
    this(rows, columns, rows * (columns / 1000), 0.2, 0.5);
  }

  
  public SparseDoubleMatrix2D(int rows, int columns, int initialCapacity, double minLoadFactor, double maxLoadFactor) {
    setUp(rows, columns);
    this.elements = new OpenIntDoubleHashMap(initialCapacity, minLoadFactor, maxLoadFactor);
  }

  
  @Override
  public DoubleMatrix2D assign(double value) {
    // overriden for performance only
    if (this.isNoView && value == 0) {
      this.elements.clear();
    } else {
      super.assign(value);
    }
    return this;
  }

  
  @Override
  public void assign(DoubleFunction function) {
    if (this.isNoView && function instanceof Mult) { // x[i] = mult*x[i]
      this.elements.assign(function);
    } else {
      super.assign(function);
    }
  }

  
  @Override
  public DoubleMatrix2D assign(DoubleMatrix2D source) {
    // overriden for performance only
    if (!(source instanceof SparseDoubleMatrix2D)) {
      return super.assign(source);
    }
    SparseDoubleMatrix2D other = (SparseDoubleMatrix2D) source;
    if (other == this) {
      return this;
    } // nothing to do
    checkShape(other);

    if (this.isNoView && other.isNoView) { // quickest
      this.elements.assign(other.elements);
      return this;
    }
    return super.assign(source);
  }

  @Override
  public DoubleMatrix2D assign(final DoubleMatrix2D y,
                               DoubleDoubleFunction function) {
    if (!this.isNoView) {
      return super.assign(y, function);
    }

    checkShape(y);

    if (function instanceof PlusMult) { // x[i] = x[i] + alpha*y[i]
      final double alpha = ((PlusMult) function).getMultiplicator();
      if (alpha == 0) {
        return this;
      } // nothing to do
      y.forEachNonZero(
          new IntIntDoubleFunction() {
            @Override
            public double apply(int i, int j, double value) {
              setQuick(i, j, getQuick(i, j) + alpha * value);
              return value;
            }
          }
      );
      return this;
    }

    if (function == Functions.MULT) { // x[i] = x[i] * y[i]
      this.elements.forEachPair(
          new IntDoubleProcedure() {
            @Override
            public boolean apply(int key, double value) {
              int i = key / columns;
              int j = key % columns;
              double r = value * y.getQuick(i, j);
              if (r != value) {
                elements.put(key, r);
              }
              return true;
            }
          }
      );
    }

    if (function == Functions.DIV) { // x[i] = x[i] / y[i]
      this.elements.forEachPair(
          new IntDoubleProcedure() {
            @Override
            public boolean apply(int key, double value) {
              int i = key / columns;
              int j = key % columns;
              double r = value / y.getQuick(i, j);
              if (r != value) {
                elements.put(key, r);
              }
              return true;
            }
          }
      );
    }

    return super.assign(y, function);
  }

  
  @Override
  public int cardinality() {
    return this.isNoView ? this.elements.size() : super.cardinality();
  }

  
  @Override
  public void ensureCapacity(int minCapacity) {
    this.elements.ensureCapacity(minCapacity);
  }

  @Override
  public void forEachNonZero(final org.carrot2.mahout.math.function.IntIntDoubleFunction function) {
    if (this.isNoView) {
      this.elements.forEachPair(
          new IntDoubleProcedure() {
            @Override
            public boolean apply(int key, double value) {
              int i = key / columns;
              int j = key % columns;
              double r = function.apply(i, j, value);
              if (r != value) {
                elements.put(key, r);
              }
              return true;
            }
          }
      );
    } else {
      super.forEachNonZero(function);
    }
  }

  
  @Override
  public double getQuick(int row, int column) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //return this.elements.get(index(row,column));
    //manually inlined:
    return this.elements.get(rowZero + row * rowStride + columnZero + column * columnStride);
  }

  
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

  
  @Override
  protected int index(int row, int column) {
    // return super.index(row,column);
    // manually inlined for speed:
    return rowZero + row * rowStride + columnZero + column * columnStride;
  }

  
  @Override
  public DoubleMatrix2D like(int rows, int columns) {
    return new SparseDoubleMatrix2D(rows, columns);
  }

  
  @Override
  public DoubleMatrix1D like1D(int size) {
    return new SparseDoubleMatrix1D(size);
  }

  
  @Override
  protected DoubleMatrix1D like1D(int size, int offset, int stride) {
    return new SparseDoubleMatrix1D(size, this.elements, offset, stride);
  }

  
  @Override
  public void setQuick(int row, int column, double value) {
    //if (debug) if (column<0 || column>=columns || row<0 || row>=rows)
    // throw new IndexOutOfBoundsException("row:"+row+", column:"+column);
    //int index =  index(row,column);
    //manually inlined:
    int index = rowZero + row * rowStride + columnZero + column * columnStride;

    //if (value == 0 || Math.abs(value) < TOLERANCE)
    if (value == 0) {
      this.elements.removeKey(index);
    } else {
      this.elements.put(index, value);
    }
  }

  
  @Override
  protected DoubleMatrix2D viewSelectionLike(int[] rowOffsets, int[] columnOffsets) {
    return new SelectedSparseDoubleMatrix2D(this.elements, rowOffsets, columnOffsets, 0);
  }

  @Override
  public DoubleMatrix1D zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha, double beta, final boolean transposeA) {
    int m = rows;
    int n = columns;
    if (transposeA) {
      m = columns;
      n = rows;
    }

    boolean ignore = z == null;
    if (ignore) {
      z = new DenseDoubleMatrix1D(m);
    }

    if (!(this.isNoView && y instanceof DenseDoubleMatrix1D && z instanceof DenseDoubleMatrix1D)) {
      return super.zMult(y, z, alpha, beta, transposeA);
    }

    if (n != y.size() || m > z.size()) {
      throw new IllegalArgumentException("Incompatible args");
    }

    if (!ignore) {
      z.assign(Functions.mult(beta / alpha));
    }

    DenseDoubleMatrix1D zz = (DenseDoubleMatrix1D) z;
    final double[] zElements = zz.elements;
    final int zStride = zz.stride;
    final int zi = z.index(0);

    DenseDoubleMatrix1D yy = (DenseDoubleMatrix1D) y;
    final double[] yElements = yy.elements;
    final int yStride = yy.stride;
    final int yi = y.index(0);

    if (yElements == null || zElements == null) {
      throw new IllegalStateException();
    }

    this.elements.forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int key, double value) {
            int i = key / columns;
            int j = key % columns;
            if (transposeA) {
              int tmp = i;
              i = j;
              j = tmp;
            }
            zElements[zi + zStride * i] += value * yElements[yi + yStride * j];
            return true;
          }
        }
    );

    if (alpha != 1.0) {
      z.assign(Functions.mult(alpha));
    }
    return z;
  }

  @Override
  public DoubleMatrix2D zMult(DoubleMatrix2D B, DoubleMatrix2D C, final double alpha, double beta,
                              final boolean transposeA, boolean transposeB) {
    if (!this.isNoView) {
      return super.zMult(B, C, alpha, beta, transposeA, transposeB);
    }
    if (transposeB) {
      B = B.viewDice();
    }
    int m = rows;
    int n = columns;
    if (transposeA) {
      m = columns;
      n = rows;
    }
    int p = B.columns;
    boolean ignore = C == null;
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

    if (!ignore) {
      C.assign(Functions.mult(beta));
    }

    // cache views
    final DoubleMatrix1D[] Brows = new DoubleMatrix1D[n];
    for (int i = n; --i >= 0;) {
      Brows[i] = B.viewRow(i);
    }
    final DoubleMatrix1D[] Crows = new DoubleMatrix1D[m];
    for (int i = m; --i >= 0;) {
      Crows[i] = C.viewRow(i);
    }

    final PlusMult fun = PlusMult.plusMult(0);

    this.elements.forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int key, double value) {
            int i = key / columns;
            int j = key % columns;
            fun.setMultiplicator(value * alpha);
            if (transposeA) {
              Crows[j].assign(Brows[i], fun);
            } else {
              Crows[i].assign(Brows[j], fun);
            }
            return true;
          }
        }
    );

    return C;
  }
}
