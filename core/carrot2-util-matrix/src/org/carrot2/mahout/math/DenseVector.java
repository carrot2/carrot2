
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

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.PlusMult;
import org.carrot2.shaded.guava.common.collect.AbstractIterator;


public class DenseVector extends AbstractVector {

  private double[] values;

  
  public DenseVector() {
    super(0);
  }

  
  public DenseVector(double[] values) {
    this(values, false);
  }

  public DenseVector(double[] values, boolean shallowCopy) {
    super(values.length);
    this.values = shallowCopy ? values : values.clone();
  }

  public DenseVector(DenseVector values, boolean shallowCopy) {
    this(values.values, shallowCopy);
  }

  
  public DenseVector(int cardinality) {
    super(cardinality);
    this.values = new double[cardinality];
  }

  
  public DenseVector(Vector vector) {
    super(vector.size());
    values = new double[vector.size()];
    Iterator<Element> it = vector.iterateNonZero();
    while (it.hasNext()) {
      Element e = it.next();
      values[e.index()] = e.get();
    }
  }

  @Override
  public DenseVector clone() {
    return new DenseVector(values.clone());
  }

  
  @Override
  public boolean isDense() {
    return true;
  }

  
  @Override
  public boolean isSequentialAccess() {
    return true;
  }

  @Override
  public double dotSelf() {
    double result = 0.0;
    int max = size();
    for (int i = 0; i < max; i++) {
      double value = this.getQuick(i);
      result += value * value;
    }
    return result;
  }

  @Override
  public double getQuick(int index) {
    return values[index];
  }

  @Override
  public DenseVector like() {
    return new DenseVector(size());
  }

  @Override
  public void setQuick(int index, double value) {
    lengthSquared = -1.0;
    values[index] = value;
  }
  
  @Override
  public Vector assign(double value) {
    this.lengthSquared = -1;
    Arrays.fill(values, value);
    return this;
  }
  
  @Override
  public Vector assign(Vector other, DoubleDoubleFunction function) {
    if (size() != other.size()) {
      throw new CardinalityException(size(), other.size());
    }
    // is there some other way to know if function.apply(0, x) = x for all x?
    if (function instanceof PlusMult) {
      Iterator<Element> it = other.iterateNonZero();
      Element e;
      while (it.hasNext() && (e = it.next()) != null) {
        values[e.index()] = function.apply(values[e.index()], e.get());
      }
    } else {
      for (int i = 0; i < size(); i++) {
        values[i] = function.apply(values[i], other.getQuick(i));
      }
    }
    lengthSquared = -1;
    return this;
  }

  public Vector assign(DenseVector vector) {
    // make sure the data field has the correct length
    if (vector.values.length != this.values.length) {
      this.values = new double[vector.values.length];
    }
    // now copy the values
    System.arraycopy(vector.values, 0, this.values, 0, this.values.length);
    return this;
  }

  @Override
  public int getNumNondefaultElements() {
    return values.length;
  }

  @Override
  public Vector viewPart(int offset, int length) {
    if (offset < 0) {
      throw new IndexException(offset, size());
    }
    if (offset + length > size()) {
      throw new IndexException(offset + length, size());
    }
    return new VectorView(this, offset, length);
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
  public boolean equals(Object o) {
    if (o instanceof DenseVector) {
      // Speedup for DenseVectors
      return Arrays.equals(values, ((DenseVector) o).values);
    }
    return super.equals(o);
  }

  @Override
  public double getLengthSquared() {
    if (lengthSquared >= 0.0) {
      return lengthSquared;
    }

    double result = 0.0;
    for (double value : values) {
      result += value * value;

    }
    lengthSquared = result;
    return result;
  }

  public void addAll(Vector v) {
    if (size() != v.size()) {
      throw new CardinalityException(size(), v.size());
    }
    
    Iterator<Element> iter = v.iterateNonZero();
    while (iter.hasNext()) {
      Element element = iter.next();
      values[element.index()] += element.get();
    }
  }

  private final class NonDefaultIterator extends AbstractIterator<Element> {

    private final DenseElement element = new DenseElement();
    private int index = 0;

    @Override
    protected Element computeNext() {
      while (index < size() && values[index] == 0.0) {
        index++;
      }
      if (index < size()) {
        element.index = index;
        index++;
        return element;
      } else {
        return endOfData();
      }
    }

  }

  private final class AllIterator extends AbstractIterator<Element> {

    private final DenseElement element = new DenseElement();

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

  private final class DenseElement implements Element {

    int index;

    @Override
    public double get() {
      return values[index];
    }

    @Override
    public int index() {
      return index;
    }

    @Override
    public void set(double value) {
      lengthSquared = -1;
      values[index] = value;
    }
  }

}
