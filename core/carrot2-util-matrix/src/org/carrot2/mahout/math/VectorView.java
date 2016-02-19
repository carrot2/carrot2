
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

import org.carrot2.shaded.guava.common.collect.AbstractIterator;


public class VectorView extends AbstractVector {

  private Vector vector;

  // the offset into the Vector
  private int offset;

  
  public VectorView() {
    super(0);
  }

  public VectorView(Vector vector, int offset, int cardinality) {
    super(cardinality);
    this.vector = vector;
    this.offset = offset;
  }

  @Override
  public Vector clone() {
    VectorView r = (VectorView) super.clone();
    r.vector = vector.clone();
    r.offset = offset;
    return r;
  }

  @Override
  public boolean isDense() {
    return vector.isDense();
  }

  @Override
  public boolean isSequentialAccess() {
    return vector.isSequentialAccess();
  }

  @Override
  public VectorView like() {
    return new VectorView(vector.like(), offset, size());
  }

  @Override
  public double getQuick(int index) {
    return vector.getQuick(offset + index);
  }

  @Override
  public void setQuick(int index, double value) {
    vector.setQuick(offset + index, value);
  }

  @Override
  public int getNumNondefaultElements() {
    return size();
  }

  @Override
  public Vector viewPart(int offset, int length) {
    if (offset < 0) {
      throw new IndexException(offset, size());
    }
    if (offset + length > size()) {
      throw new IndexException(offset + length, size());
    }
    return new VectorView(vector, offset + this.offset, length);
  }

  
  private boolean isInView(int index) {
    return index >= offset && index < offset + size();
  }

  @Override
  public Iterator<Element> iterateNonZero() {
    return new NonZeroIterator();
  }

  @Override
  public Iterator<Element> iterator() {
    return new AllIterator();
  }

  public final class NonZeroIterator extends AbstractIterator<Element> {

    private final Iterator<Element> it;

    private NonZeroIterator() {
      it = vector.iterateNonZero();
    }

    @Override
    protected Element computeNext() {
      while (it.hasNext()) {
        Element el = it.next();
        if (isInView(el.index()) && el.get() != 0) {
          Element decorated = vector.getElement(el.index());
          return new DecoratorElement(decorated);
        }
      }
      return endOfData();
    }

  }

  public final class AllIterator extends AbstractIterator<Element> {

    private final Iterator<Element> it;

    private AllIterator() {
      it = vector.iterator();
    }

    @Override
    protected Element computeNext() {
      while (it.hasNext()) {
        Element el = it.next();
        if (isInView(el.index())) {
          Element decorated = vector.getElement(el.index());
          return new DecoratorElement(decorated);
        }
      }
      return endOfData(); // No element was found
    }

  }

  private final class DecoratorElement implements Element {

    private final Element decorated;

    private DecoratorElement(Element decorated) {
      this.decorated = decorated;
    }

    @Override
    public double get() {
      return decorated.get();
    }

    @Override
    public int index() {
      return decorated.index() - offset;
    }

    @Override
    public void set(double value) {
      decorated.set(value);
    }
  }

  @Override
  public double getLengthSquared() {
    double result = 0.0;
    int size = size();
    for (int i = 0; i < size; i++) {
      double value = getQuick(i);
      result += value * value;
    }
    return result;
  }

  @Override
  public double getDistanceSquared(Vector v) {
    double result = 0.0;
    int size = size();
    for (int i = 0; i < size; i++) {
      double delta = getQuick(i) - v.getQuick(i);
      result += delta * delta;
    }
    return result;
  }
}
