/* removed */
/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.set;

import org.carrot2.mahout.math.PersistentObject;
import org.carrot2.mahout.math.map.PrimeFinder;

public abstract class AbstractSet extends PersistentObject {
  //public static boolean debug = false; // debug only

  /* removed */
  protected int distinct;

  /* removed */
  protected int lowWaterMark;
  protected int highWaterMark;

  /* removed */
  protected double minLoadFactor;

  /* removed */
  protected double maxLoadFactor;

  // these are public access for unit tests.
  public static final int defaultCapacity = 277;
  public static final double defaultMinLoadFactor = 0.2;
  public static final double defaultMaxLoadFactor = 0.5;

  /* removed */
  protected int chooseGrowCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((4 * size / (3 * minLoad + maxLoad)))));
  }

  /* removed */
  protected int chooseHighWaterMark(int capacity, double maxLoad) {
    return Math.min(capacity - 2, (int) (capacity * maxLoad)); //makes sure there is always at least one FREE slot
  }

  /* removed */
  protected int chooseLowWaterMark(int capacity, double minLoad) {
    return (int) (capacity * minLoad);
  }

  /* removed */
  protected int chooseMeanCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((2 * size / (minLoad + maxLoad)))));
  }

  /* removed */
  protected int chooseShrinkCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((4 * size / (minLoad + 3 * maxLoad)))));
  }

  /* removed */
  public abstract void clear();

  /* removed */
  public void ensureCapacity(int minCapacity) {
  }

  /* removed */
  public boolean isEmpty() {
    return distinct == 0;
  }

  /* removed */
  protected int nextPrime(int desiredCapacity) {
    return PrimeFinder.nextPrime(desiredCapacity);
  }

  /* removed */
  protected void setUp(int initialCapacity, double minLoadFactor, double maxLoadFactor) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Initial Capacity must not be less than zero: " + initialCapacity);
    }
    if (minLoadFactor < 0.0 || minLoadFactor >= 1.0) {
      throw new IllegalArgumentException("Illegal minLoadFactor: " + minLoadFactor);
    }
    if (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) {
      throw new IllegalArgumentException("Illegal maxLoadFactor: " + maxLoadFactor);
    }
    if (minLoadFactor >= maxLoadFactor) {
      throw new IllegalArgumentException(
          "Illegal minLoadFactor: " + minLoadFactor + " and maxLoadFactor: " + maxLoadFactor);
    }
  }

  /* removed */
  public int size() {
    return distinct;
  }

  /* removed */
  public void trimToSize() {
  }
  
  protected static boolean equalsMindTheNull(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.equals(b);
  }
}
