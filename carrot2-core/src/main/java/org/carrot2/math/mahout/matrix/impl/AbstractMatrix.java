/* Imported from Mahout. */package org.carrot2.math.mahout.matrix.impl;



public abstract class AbstractMatrix {

  protected boolean isNoView = true;

  
  protected AbstractMatrix() {
  }

  
  public void ensureCapacity(int minNonZeros) {
  }

  
  public abstract int size();

}
