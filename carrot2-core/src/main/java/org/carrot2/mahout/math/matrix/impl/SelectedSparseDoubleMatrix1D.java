/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.map.AbstractIntDoubleMap;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;


final class SelectedSparseDoubleMatrix1D extends DoubleMatrix1D {
  /*
   * The elements of the matrix.
   */
  final AbstractIntDoubleMap elements;

  
  private final int[] offsets;

  
  private int offset;

  
  SelectedSparseDoubleMatrix1D(int size, AbstractIntDoubleMap elements, int zero, int stride, int[] offsets,
                                         int offset) {
    setUp(size, zero, stride);

    this.elements = elements;
    this.offsets = offsets;
    this.offset = offset;
    this.isNoView = false;
  }

  
  SelectedSparseDoubleMatrix1D(AbstractIntDoubleMap elements, int[] offsets) {
    this(offsets.length, elements, 0, 1, offsets, 0);
  }

  
  @Override
  protected int offset(int absRank) {
    return offsets[absRank];
  }

  
  @Override
  public double getQuick(int index) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //return elements.get(index(index));
    //manually inlined:
    return elements.get(offset + offsets[zero + index * stride]);
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
    //return this.offset + super.index(rank);
    // manually inlined:
    return offset + offsets[zero + rank * stride];
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
    //manually inlined:
    int i = offset + offsets[zero + index * stride];
    if (value == 0) {
      this.elements.removeKey(i);
    } else {
      this.elements.put(i, value);
    }
  }

  
  @Override
  protected void setUp(int size) {
    super.setUp(size);
    this.stride = 1;
    this.offset = 0;
  }

  
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    return new SelectedSparseDoubleMatrix1D(this.elements, offsets);
  }
}
