
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

import java.util.Arrays;
import java.util.Iterator;

import org.carrot2.mahout.math.function.Functions;
import org.carrot2.shaded.guava.common.collect.AbstractIterator;
import org.carrot2.shaded.guava.common.primitives.Doubles;


public class SequentialAccessSparseVector extends AbstractVector {

  private OrderedIntDoubleMapping values;

  
  public SequentialAccessSparseVector() {
    super(0);
  }

  public SequentialAccessSparseVector(int cardinality) {
    this(cardinality, cardinality / 8); // arbitrary estimate of 'sparseness'
  }

  public SequentialAccessSparseVector(int cardinality, int size) {
    super(cardinality);
    values = new OrderedIntDoubleMapping(size);
  }

  public SequentialAccessSparseVector(Vector other) {
    this(other.size(), other.getNumNondefaultElements());

    if (other.isSequentialAccess()) {
      Iterator<Element> it = other.iterateNonZero();
      Element e;
      while (it.hasNext() && (e = it.next()) != null) {
        set(e.index(), e.get());
      }
    } else {
      // If the incoming Vector to copy is random, then adding items
      // from the Iterator can degrade performance dramatically if
      // the number of elements is large as this Vector tries to stay
      // in order as items are added, so it's better to sort the other
      // Vector's elements by index and then add them to this
      copySortedRandomAccessSparseVector(other);
    }    
  }

  // Sorts a RandomAccessSparseVectors Elements before adding them to this
  private int copySortedRandomAccessSparseVector(Vector other) {
    int elementCount = other.getNumNondefaultElements();
    OrderedElement[] sortableElements = new OrderedElement[elementCount];
    Iterator<Element> it = other.iterateNonZero();
    Element e;
    int s=0;
    while (it.hasNext() && (e = it.next()) != null) {
      sortableElements[s++] = new OrderedElement(e.index(), e.get());
    }
    Arrays.sort(sortableElements);
    for (int i = 0; i < sortableElements.length; i++) {
      values.getIndices()[i] = sortableElements[i].index;
      values.getValues()[i] = sortableElements[i].value;
    }
    values = new OrderedIntDoubleMapping(values.getIndices(), values.getValues(), elementCount);
    return elementCount;
  }

  public SequentialAccessSparseVector(SequentialAccessSparseVector other, boolean shallowCopy) {
    super(other.size());
    values = shallowCopy ? other.values : other.values.clone();
  }

  public SequentialAccessSparseVector(SequentialAccessSparseVector other) {
    this(other.size(), other.getNumNondefaultElements());
    values = other.values.clone();
  }

  private SequentialAccessSparseVector(int cardinality, OrderedIntDoubleMapping values) {
    super(cardinality);
    this.values = values;
  }

  @Override
  public SequentialAccessSparseVector clone() {
    return new SequentialAccessSparseVector(size(), values.clone());
  }

  @Override
  public Vector assign(Vector other) {
    int size = size();
    if (size != other.size()) {
      throw new CardinalityException(size, other.size());
    }
    if (other instanceof SequentialAccessSparseVector) {
      values = ((SequentialAccessSparseVector)other).values.clone();
    } else {
      values = new OrderedIntDoubleMapping();
      Iterator<Element> othersElems = other.iterateNonZero();
      while (othersElems.hasNext()) {
        Element elem = othersElems.next();
        setQuick(elem.index(), elem.get());
      }
    }
    return this;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append('{');
    Iterator<Element> it = iterateNonZero();
    while (it.hasNext()) {
      Element e = it.next();
      result.append(e.index());
      result.append(':');
      result.append(e.get());
      result.append(',');
    }
    if (result.length() > 1) {
      result.setCharAt(result.length() - 1, '}');
    }
    return result.toString();
  }

  
  @Override
  public boolean isDense() {
    return false;
  }

  
  @Override
  public boolean isSequentialAccess() {
    return true;
  }

  @Override
  public double getQuick(int index) {
    return values.get(index);
  }

  @Override
  public void setQuick(int index, double value) {
    lengthSquared = -1;
    values.set(index, value);
  }

  @Override
  public int getNumNondefaultElements() {
    return values.getNumMappings();
  }

  @Override
  public SequentialAccessSparseVector like() {
    return new SequentialAccessSparseVector(size(), values.getNumMappings());
  }

  @Override
  public Iterator<Element> iterateNonZero() {
    return new NonDefaultIterator();
  }

  @Override
  public Iterator<Element> iterator() {
    return new AllIterator();
  }

  @Override
  public Vector minus(Vector that) {
    if (size() != that.size()) {
      throw new CardinalityException(size(), that.size());
    }
    // Here we compute "that - this" since it's not fast to randomly access "this"
    // and then invert at the end
    Vector result = that.clone();
    Iterator<Element> iter = this.iterateNonZero();
    while (iter.hasNext()) {
      Element thisElement = iter.next();
      int index = thisElement.index();
      result.setQuick(index, that.getQuick(index) - thisElement.get());
    }
    result.assign(Functions.NEGATE);
    return result;
  }


  private final class NonDefaultIterator extends AbstractIterator<Element> {

    private final NonDefaultElement element = new NonDefaultElement();

    @Override
    protected Element computeNext() {
      int numMappings = values.getNumMappings();
      if (numMappings <= 0 || element.getNextOffset() >= numMappings) {
        return endOfData();
      }
      element.advanceOffset();
      return element;
    }

  }

  private final class AllIterator extends AbstractIterator<Element> {

    private final AllElement element = new AllElement();

    @Override
    protected Element computeNext() {
      int numMappings = values.getNumMappings();
      if (numMappings <= 0 || element.getNextIndex() > values.getIndices()[numMappings - 1]) {
        return endOfData();
      }
      element.advanceIndex();
      return element;
    }

  }

  private final class NonDefaultElement implements Element {

    private int offset = -1;

    void advanceOffset() {
      offset++;
    }

    int getNextOffset() {
      return offset + 1;
    }

    @Override
    public double get() {
      return values.getValues()[offset];
    }

    @Override
    public int index() {
      return values.getIndices()[offset];
    }

    @Override
    public void set(double value) {
      lengthSquared = -1;
      values.getValues()[offset] = value;
    }
  }

  private final class AllElement implements Element {

    private int index = -1;
    private int nextOffset;

    void advanceIndex() {
      index++;
      if (index > values.getIndices()[nextOffset]) {
        nextOffset++;
      }
    }

    int getNextIndex() {
      return index + 1;
    }

    @Override
    public double get() {
      if (index == values.getIndices()[nextOffset]) {
        return values.getValues()[nextOffset];
      }
      return OrderedIntDoubleMapping.DEFAULT_VALUE;
    }

    @Override
    public int index() {
      return index;
    }

    @Override
    public void set(double value) {
      lengthSquared = -1;
      if (index == values.getIndices()[nextOffset]) {
        values.getValues()[nextOffset] = value;
      } else {
        // Yes, this works; the offset into indices of the new value's index will still be nextOffset
        values.set(index, value);
      }
    }
  }

  // Comparable Element for sorting Elements by index
  private static final class OrderedElement implements Comparable<OrderedElement> {
    private final int index;
    private final double value;
    
    OrderedElement(int index, double value) {
      this.index = index;
      this.value = value;
    }

    @Override
    public int compareTo(OrderedElement that) {
      // both indexes are positive, and neither can be Integer.MAX_VALUE (otherwise there would be
      // an array somewhere with Integer.MAX_VALUE + 1 elements)
      return this.index - that.index;
    }

    @Override
    public int hashCode() {
      return index ^ Doubles.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof OrderedElement)) {
        return false;
      }
      OrderedElement other = (OrderedElement) o;
      return index == other.index && value == other.value;
    }

  }
  
}
