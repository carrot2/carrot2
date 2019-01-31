/* Imported from Mahout. */package org.carrot2.mahout.math.set;

import org.carrot2.mahout.math.PersistentObject;
import org.carrot2.mahout.math.map.PrimeFinder;

public abstract class AbstractSet extends PersistentObject {
  //public static boolean debug = false; // debug only

  
  protected int distinct;

  
  protected int lowWaterMark;
  protected int highWaterMark;

  
  protected double minLoadFactor;

  
  protected double maxLoadFactor;

  // these are public access for unit tests.
  public static final int defaultCapacity = 277;
  public static final double defaultMinLoadFactor = 0.2;
  public static final double defaultMaxLoadFactor = 0.5;

  
  protected int chooseGrowCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((4 * size / (3 * minLoad + maxLoad)))));
  }

  
  protected int chooseHighWaterMark(int capacity, double maxLoad) {
    return Math.min(capacity - 2, (int) (capacity * maxLoad)); //makes sure there is always at least one FREE slot
  }

  
  protected int chooseLowWaterMark(int capacity, double minLoad) {
    return (int) (capacity * minLoad);
  }

  
  protected int chooseMeanCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((2 * size / (minLoad + maxLoad)))));
  }

  
  protected int chooseShrinkCapacity(int size, double minLoad, double maxLoad) {
    return nextPrime(Math.max(size + 1, (int) ((4 * size / (minLoad + 3 * maxLoad)))));
  }

  
  public abstract void clear();

  
  public void ensureCapacity(int minCapacity) {
  }

  
  public boolean isEmpty() {
    return distinct == 0;
  }

  
  protected int nextPrime(int desiredCapacity) {
    return PrimeFinder.nextPrime(desiredCapacity);
  }

  
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

  
  public int size() {
    return distinct;
  }

  
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
