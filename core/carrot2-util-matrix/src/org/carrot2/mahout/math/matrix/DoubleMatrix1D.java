/* Imported from Mahout. */package org.carrot2.mahout.math.matrix;

import org.carrot2.mahout.math.DenseVector;
import org.carrot2.mahout.math.Vector;
import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.PlusMult;
import org.carrot2.mahout.math.list.DoubleArrayList;
import org.carrot2.mahout.math.list.IntArrayList;
import org.carrot2.mahout.math.matrix.impl.AbstractMatrix1D;

public abstract class DoubleMatrix1D extends AbstractMatrix1D implements Cloneable {

  
  protected DoubleMatrix1D() {
  }

  public double aggregate(DoubleDoubleFunction aggr,
                          DoubleFunction f) {
    if (size == 0) {
      return Double.NaN;
    }
    double a = f.apply(getQuick(size - 1));
    for (int i = size - 1; --i >= 0;) {
      a = aggr.apply(a, f.apply(getQuick(i)));
    }
    return a;
  }

  public double aggregate(DoubleMatrix1D other, DoubleDoubleFunction aggr,
                          DoubleDoubleFunction f) {
    checkSize(other);
    if (size == 0) {
      return Double.NaN;
    }
    double a = f.apply(getQuick(size - 1), other.getQuick(size - 1));
    for (int i = size - 1; --i >= 0;) {
      a = aggr.apply(a, f.apply(getQuick(i), other.getQuick(i)));
    }
    return a;
  }

  public void assign(double[] values) {
    if (values.length != size) {
      throw new IllegalArgumentException(
          "Must have same number of cells: length=" + values.length + "size()=" + size());
    }
    for (int i = size; --i >= 0;) {
      setQuick(i, values[i]);
    }
  }

  public void assign(double value) {
    for (int i = size; --i >= 0;) {
      setQuick(i, value);
    }
  }

  public void assign(DoubleFunction function) {
    for (int i = size; --i >= 0;) {
      setQuick(i, function.apply(getQuick(i)));
    }
  }

  public DoubleMatrix1D assign(DoubleMatrix1D other) {
    if (other == this) {
      return this;
    }
    checkSize(other);
    if (haveSharedCells(other)) {
      other = other.copy();
    }

    for (int i = size; --i >= 0;) {
      setQuick(i, other.getQuick(i));
    }
    return this;
  }

  public Vector toVector() {
    Vector vector = new DenseVector(cardinality());
    for (int i = 0; i < cardinality(); i++) {
      vector.set(i, get(i));
    }
    return vector;
  }

  public DoubleMatrix1D assign(DoubleMatrix1D y, DoubleDoubleFunction function) {
    checkSize(y);
    for (int i = size; --i >= 0;) {
      setQuick(i, function.apply(getQuick(i), y.getQuick(i)));
    }
    return this;
  }

  public void assign(DoubleMatrix1D y, DoubleDoubleFunction function,
                     IntArrayList nonZeroIndexes) {
    checkSize(y);
    int[] nonZeroElements = nonZeroIndexes.elements();

    // specialized for speed
    if (function == Functions.MULT) {  // x[i] = x[i] * y[i]
      int j = 0;
      for (int index = nonZeroIndexes.size(); --index >= 0;) {
        int i = nonZeroElements[index];
        for (; j < i; j++) {
          setQuick(j, 0);
        } // x[i] = 0 for all zeros
        setQuick(i, getQuick(i) * y.getQuick(i));  // x[i] * y[i] for all nonZeros
        j++;
      }
    } else if (function instanceof PlusMult) {
      double multiplicator = ((PlusMult) function).getMultiplicator();
      if (multiplicator == 0.0) { // x[i] = x[i] + 0*y[i]
        // do nothing
      } else if (multiplicator == 1.0) { // x[i] = x[i] + y[i]
        for (int index = nonZeroIndexes.size(); --index >= 0;) {
          int i = nonZeroElements[index];
          setQuick(i, getQuick(i) + y.getQuick(i));
        }
      } else if (multiplicator == -1.0) { // x[i] = x[i] - y[i]
        for (int index = nonZeroIndexes.size(); --index >= 0;) {
          int i = nonZeroElements[index];
          setQuick(i, getQuick(i) - y.getQuick(i));
        }
      } else { // the general case x[i] = x[i] + mult*y[i]
        for (int index = nonZeroIndexes.size(); --index >= 0;) {
          int i = nonZeroElements[index];
          setQuick(i, getQuick(i) + multiplicator * y.getQuick(i));
        }
      }
    } else { // the general case x[i] = f(x[i],y[i])
      assign(y, function);
    }
  }

  
  public int cardinality() {
    int cardinality = 0;
    for (int i = size; --i >= 0;) {
      if (getQuick(i) != 0) {
        cardinality++;
      }
    }
    return cardinality;
  }

  
  protected int cardinality(int maxCardinality) {
    int cardinality = 0;
    int i = size;
    while (--i >= 0 && cardinality < maxCardinality) {
      if (getQuick(i) != 0) {
        cardinality++;
      }
    }
    return cardinality;
  }

  
  public DoubleMatrix1D copy() {
    DoubleMatrix1D copy = like();
    copy.assign(this);
    return copy;
  }

  
  public boolean equals(double value) {
    return org.carrot2.mahout.math.matrix.linalg.Property.DEFAULT.equals(this, value);
  }

  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DoubleMatrix1D)) {
      return false;
    }

    return org.carrot2.mahout.math.matrix.linalg.Property.DEFAULT.equals(this, (DoubleMatrix1D) obj);
  }

  public double get(int index) {
    if (index < 0 || index >= size) {
      checkIndex(index);
    }
    return getQuick(index);
  }

  
  protected DoubleMatrix1D getContent() {
    return this;
  }

  public void getNonZeros(IntArrayList indexList, DoubleArrayList valueList) {
    boolean fillIndexList = indexList != null;
    boolean fillValueList = valueList != null;
    if (fillIndexList) {
      indexList.clear();
    }
    if (fillValueList) {
      valueList.clear();
    }
    int s = size;
    for (int i = 0; i < s; i++) {
      double value = getQuick(i);
      if (value != 0) {
        if (fillIndexList) {
          indexList.add(i);
        }
        if (fillValueList) {
          valueList.add(value);
        }
      }
    }
  }

  public void getNonZeros(IntArrayList indexList, DoubleArrayList valueList, int maxCardinality) {
    boolean fillIndexList = indexList != null;
    boolean fillValueList = valueList != null;
    int card = cardinality(maxCardinality);
    if (fillIndexList) {
      indexList.setSize(card);
    }
    if (fillValueList) {
      valueList.setSize(card);
    }
    if (!(card < maxCardinality)) {
      return;
    }

    if (fillIndexList) {
      indexList.setSize(0);
    }
    if (fillValueList) {
      valueList.setSize(0);
    }
    int s = size;
    for (int i = 0; i < s; i++) {
      double value = getQuick(i);
      if (value != 0) {
        if (fillIndexList) {
          indexList.add(i);
        }
        if (fillValueList) {
          valueList.add(value);
        }
      }
    }
  }

  public abstract double getQuick(int index);

  protected boolean haveSharedCells(DoubleMatrix1D other) {
    if (other == null) {
      return false;
    }
    if (this == other) {
      return true;
    }
    return getContent().haveSharedCellsRaw(other.getContent());
  }

  protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
    return false;
  }

  
  public DoubleMatrix1D like() {
    return like(size);
  }

  
  public abstract DoubleMatrix1D like(int size);

  
  public abstract DoubleMatrix2D like2D(int rows, int columns);

  
  public void set(int index, double value) {
    if (index < 0 || index >= size) {
      checkIndex(index);
    }
    setQuick(index, value);
  }

  
  public abstract void setQuick(int index, double value);

  
  public void swap(DoubleMatrix1D other) {
    checkSize(other);
    for (int i = size; --i >= 0;) {
      double tmp = getQuick(i);
      setQuick(i, other.getQuick(i));
      other.setQuick(i, tmp);
    }
  }

  
  public double[] toArray() {
    double[] values = new double[size];
    toArray(values);
    return values;
  }

  
  public void toArray(double[] values) {
    if (values.length < size) {
      throw new IllegalArgumentException("values too small");
    }
    for (int i = size; --i >= 0;) {
      values[i] = getQuick(i);
    }
  }

  
  protected DoubleMatrix1D view() {
    try {
      return (DoubleMatrix1D) clone();
    } catch (CloneNotSupportedException cnse) {
      throw new IllegalStateException();
    }
  }

  
  public DoubleMatrix1D viewPart(int index, int width) {
    return (DoubleMatrix1D) view().vPart(index, width);
  }

  
  protected abstract DoubleMatrix1D viewSelectionLike(int[] offsets);

  
  public double zDotProduct(DoubleMatrix1D y) {
    return zDotProduct(y, 0, size);
  }

  
  public double zDotProduct(DoubleMatrix1D y, int from, int length) {
    if (from < 0 || length <= 0) {
      return 0;
    }

    int tail = from + length;
    if (size < tail) {
      tail = size;
    }
    if (y.size < tail) {
      tail = y.size;
    }
    length = tail - from;

    double sum = 0;
    int i = tail - 1;
    for (int k = length; --k >= 0; i--) {
      sum += getQuick(i) * y.getQuick(i);
    }
    return sum;
  }

  
  public double zDotProduct(DoubleMatrix1D y, int from, int length, IntArrayList nonZeroIndexes) {
    // determine minimum length
    if (from < 0 || length <= 0) {
      return 0;
    }

    int tail = from + length;
    if (size < tail) {
      tail = size;
    }
    if (y.size < tail) {
      tail = y.size;
    }
    length = tail - from;
    if (length <= 0) {
      return 0;
    }

    // setup
    int[] nonZeroIndexElements = nonZeroIndexes.elements();
    int index = 0;
    int s = nonZeroIndexes.size();

    // skip to start
    while (index < s && nonZeroIndexElements[index] < from) {
      index++;
    }

    // now the sparse dot product
    int i;
    double sum = 0;
    while (--length >= 0 && index < s && (i = nonZeroIndexElements[index]) < tail) {
      sum += getQuick(i) * y.getQuick(i);
      index++;
    }

    return sum;
  }

  
  protected double zDotProduct(DoubleMatrix1D y, IntArrayList nonZeroIndexes) {
    return zDotProduct(y, 0, size, nonZeroIndexes);
    /*
    double sum = 0;
    int[] nonZeroIndexElements = nonZeroIndexes.elements();
    for (int index=nonZeroIndexes.size(); --index >= 0; ) {
      int i = nonZeroIndexElements[index];
      sum += getQuick(i) * y.getQuick(i);
    }
    return sum;
    */
  }

  
  public double zSum() {
    if (size() == 0) {
      return 0;
    }
    return aggregate(Functions.PLUS, Functions.IDENTITY);
  }
}
