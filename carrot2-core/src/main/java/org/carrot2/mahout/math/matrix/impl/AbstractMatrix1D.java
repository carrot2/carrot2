/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;



public abstract class AbstractMatrix1D extends AbstractMatrix {

  
  protected int size;
  
  protected int zero;
  
  protected int stride;

  
  protected AbstractMatrix1D() {
  }

  
  protected int offset(int absRank) {
    return absRank;
  }

  
  protected int rank(int rank) {
    return zero + rank * stride;
    //return zero + ((rank+flipMask)^flipMask);
    //return zero + rank*flip; // slower
  }

  
  protected void checkIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Attempted to access at index=" + index);
    }
  }

  
  protected void checkRange(int index, int width) {
    if (index < 0 || index + width > size) {
      throw new IndexOutOfBoundsException("index: " + index + ", width: " + width + ", size=" + size);
    }
  }

  
  public void checkSize(AbstractMatrix1D b) {
    if (size != b.size) {
      throw new IllegalArgumentException("Incompatible sizes: " + size + " and " + b.size);
    }
  }

  
  protected int index(int rank) {
    return offset(rank(rank));
  }

  
  protected void setUp(int size) {
    setUp(size, 0, 1);
  }

  
  protected void setUp(int size, int zero, int stride) {
    if (size < 0) {
      throw new IllegalArgumentException("negative size");
    }

    this.size = size;
    this.zero = zero;
    this.stride = stride;
    this.isNoView = true;
  }

  
  @Override
  public int size() {
    return size;
  }

  
  protected int stride(int dimension) {
    if (dimension != 0) {
      throw new IllegalArgumentException("invalid dimension: " + dimension);
    }
    return this.stride;
  }

  
  protected AbstractMatrix1D vPart(int index, int width) {
    checkRange(index, width);
    this.zero += this.stride * index;
    this.size = width;
    this.isNoView = false;
    return this;
  }

}
