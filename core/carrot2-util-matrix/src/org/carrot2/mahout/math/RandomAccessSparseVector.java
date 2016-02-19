
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.mahout.math;

import java.util.Iterator;

import org.carrot2.mahout.math.list.IntArrayList;
import org.carrot2.mahout.math.map.OpenIntDoubleHashMap;
import org.carrot2.shaded.guava.common.collect.AbstractIterator;



public class RandomAccessSparseVector extends AbstractVector {

  private static final int INITIAL_CAPACITY = 11;

  private OpenIntDoubleHashMap values;

  
  public RandomAccessSparseVector() {
    super(0);
  }

  public RandomAccessSparseVector(int cardinality) {
    this(cardinality, Math.min(cardinality, INITIAL_CAPACITY)); // arbitrary estimate of 'sparseness'
  }

  public RandomAccessSparseVector(int cardinality, int initialCapacity) {
    super(cardinality);
    values = new OpenIntDoubleHashMap(initialCapacity);
  }

  public RandomAccessSparseVector(Vector other) {
    this(other.size(), other.getNumNondefaultElements());
    Iterator<Element> it = other.iterateNonZero();
    Element e;
    while (it.hasNext() && (e = it.next()) != null) {
      values.put(e.index(), e.get());
    }
  }

  private RandomAccessSparseVector(int cardinality, OpenIntDoubleHashMap values) {
    super(cardinality);
    this.values = values;
  }

  public RandomAccessSparseVector(RandomAccessSparseVector other, boolean shallowCopy) {
    super(other.size());
    values = shallowCopy ? other.values : (OpenIntDoubleHashMap)other.values.clone();
  }

  @Override
  public RandomAccessSparseVector clone() {
    return new RandomAccessSparseVector(size(), (OpenIntDoubleHashMap) values.clone());
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append('{');
    Iterator<Element> it = iterateNonZero();
    boolean first = true;
    while (it.hasNext()) {
      if (first) {
        first = false;
      } else {
        result.append(',');
      }
      Element e = it.next();
      result.append(e.index());
      result.append(':');
      result.append(e.get());
    }
    result.append('}');
    return result.toString();
  }

  @Override
  public Vector assign(Vector other) {
    if (size() != other.size()) {
      throw new CardinalityException(size(), other.size());
    }
    values.clear();
    Iterator<Element> it = other.iterateNonZero();
    Element e;
    while (it.hasNext() && (e = it.next()) != null) {
      setQuick(e.index(), e.get());
    }
    return this;
  }

  
  @Override
  public boolean isDense() {
    return false;
  }

  
  @Override
  public boolean isSequentialAccess() {
    return false;
  }

  @Override
  public double getQuick(int index) {
    return values.get(index);
  }

  @Override
  public void setQuick(int index, double value) {
    lengthSquared = -1.0;
    if (value == 0.0) {
      values.removeKey(index);
    } else {
      values.put(index, value);
    }
  }

  @Override
  public int getNumNondefaultElements() {
    return values.size();
  }

  @Override
  public RandomAccessSparseVector like() {
    return new RandomAccessSparseVector(size(), values.size());
  }

  
  @Override
  public Iterator<Element> iterateNonZero() {
    return new NonDefaultIterator();
  }
  
  @Override
  public Iterator<Element> iterator() {
    return new AllIterator();
  }

  private final class NonDefaultIterator extends AbstractIterator<Element> {

    private final RandomAccessElement element = new RandomAccessElement();
    private final IntArrayList indices = new IntArrayList();
    private int offset;

    private NonDefaultIterator() {
      values.keys(indices);
    }

    @Override
    protected Element computeNext() {
      if (offset >= indices.size()) {
        return endOfData();
      }
      element.index = indices.get(offset);
      offset++;
      return element;
    }

  }

  private final class AllIterator extends AbstractIterator<Element> {

    private final RandomAccessElement element = new RandomAccessElement();

    private AllIterator() {
      element.index = -1;
    }

    @Override
    protected Element computeNext() {
      if (element.index + 1 < size()) {
        element.index++;
        return element;
      } else {
        return endOfData();
      }
    }

  }

  private final class RandomAccessElement implements Element {

    int index;

    @Override
    public double get() {
      return values.get(index);
    }

    @Override
    public int index() {
      return index;
    }

    @Override
    public void set(double value) {
      lengthSquared = -1;
      if (value == 0.0) {
        values.removeKey(index);
      } else {
        values.put(index, value);
      }
    }
  }
  
}
