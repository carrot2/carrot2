/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.RandomAccessSparseVector;
import org.carrot2.mahout.math.Vector;
import org.carrot2.mahout.math.function.IntDoubleProcedure;
import org.carrot2.mahout.math.map.AbstractIntDoubleMap;
import org.carrot2.mahout.math.map.OpenIntDoubleHashMap;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

public final class SparseDoubleMatrix1D extends DoubleMatrix1D {
  /*
   * The elements of the matrix.
   */
  final AbstractIntDoubleMap elements;

  
  public SparseDoubleMatrix1D(double[] values) {
    this(values.length);
    assign(values);
  }

  
  public SparseDoubleMatrix1D(int size) {
    this(size, size / 1000, 0.2, 0.5);
  }

  
  public SparseDoubleMatrix1D(int size, int initialCapacity, double minLoadFactor, double maxLoadFactor) {
    setUp(size);
    this.elements = new OpenIntDoubleHashMap(initialCapacity, minLoadFactor, maxLoadFactor);
  }

  
  SparseDoubleMatrix1D(int size, AbstractIntDoubleMap elements, int offset, int stride) {
    setUp(size, offset, stride);
    this.elements = elements;
    this.isNoView = false;
  }


  @Override
  public Vector toVector() {
    final Vector vector = new RandomAccessSparseVector(cardinality());
    elements.forEachPair(new IntDoubleProcedure() {
      @Override
      public boolean apply(int i, double v) {
        vector.setQuick(i, v);
        return true;
      }
    });
    return vector;
  }


  
  @Override
  public void assign(double value) {
    // overriden for performance only
    if (this.isNoView && value == 0) {
      this.elements.clear();
    } else {
      super.assign(value);
    }
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
  public double getQuick(int index) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //return this.elements.get(index(index));
    // manually inlined:
    return elements.get(zero + index * stride);
  }

  
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

  
  @Override
  protected int index(int rank) {
    // overriden for manual inlining only
    //return _offset(_rank(rank));
    return zero + rank * stride;
  }

  
  @Override
  public DoubleMatrix1D like(int size) {
    return new SparseDoubleMatrix1D(size);
  }

  
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return new SparseDoubleMatrix2D(rows, columns);
  }

  
  @Override
  public void setQuick(int index, double value) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //int i =  index(index);
    // manually inlined:
    int i = zero + index * stride;
    if (value == 0) {
      this.elements.removeKey(i);
    } else {
      this.elements.put(i, value);
    }
  }

  
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    return new SelectedSparseDoubleMatrix1D(this.elements, offsets);
  }
}
