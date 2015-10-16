/* Imported from Mahout. */package org.carrot2.mahout.math.matrix.impl;

import org.carrot2.mahout.math.function.DoubleDoubleFunction;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.Mult;
import org.carrot2.mahout.math.function.PlusMult;
import org.carrot2.mahout.math.matrix.DoubleMatrix1D;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

public class DenseDoubleMatrix1D extends DoubleMatrix1D {

  
  protected final double[] elements;

  
  public DenseDoubleMatrix1D(double[] values) {
    this(values.length);
    assign(values);
  }

  
  public DenseDoubleMatrix1D(int size) {
    setUp(size);
    this.elements = new double[size];
  }

  
  protected DenseDoubleMatrix1D(int size, double[] elements, int zero, int stride) {
    setUp(size, zero, stride);
    this.elements = elements;
    this.isNoView = false;
  }

  
  @Override
  public void assign(double[] values) {
    if (isNoView) {
      if (values.length != size) {
        throw new IllegalArgumentException(
            "Must have same number of cells: length=" + values.length + "size()=" + size());
      }
      System.arraycopy(values, 0, this.elements, 0, values.length);
    } else {
      super.assign(values);
    }
  }

  
  @Override
  public void assign(double value) {
    int index = index(0);
    int s = this.stride;
    double[] elems = this.elements;
    for (int i = size; --i >= 0;) {
      elems[index] = value;
      index += s;
    }
  }

  
  @Override
  public void assign(DoubleFunction function) {
    int s = stride;
    int i = index(0);
    double[] elems = this.elements;
    if (elems == null) {
      throw new IllegalStateException();
    }

    // specialization for speed
    if (function instanceof Mult) { // x[i] = mult*x[i]
      double multiplicator = ((Mult) function).getMultiplicator();
      if (multiplicator == 1) {
        return;
      }
      for (int k = size; --k >= 0;) {
        elems[i] *= multiplicator;
        i += s;
      }
    } else { // the general case x[i] = f(x[i])
      for (int k = size; --k >= 0;) {
        elems[i] = function.apply(elems[i]);
        i += s;
      }
    }
  }

  
  @Override
  public DoubleMatrix1D assign(DoubleMatrix1D source) {
    // overriden for performance only
    if (!(source instanceof DenseDoubleMatrix1D)) {
      return super.assign(source);
    }
    DenseDoubleMatrix1D other = (DenseDoubleMatrix1D) source;
    if (other == this) {
      return this;
    }
    checkSize(other);
    if (isNoView && other.isNoView) { // quickest
      System.arraycopy(other.elements, 0, this.elements, 0, this.elements.length);
      return this;
    }
    if (haveSharedCells(other)) {
      DoubleMatrix1D c = other.copy();
      if (!(c instanceof DenseDoubleMatrix1D)) { // should not happen
        return super.assign(source);
      }
      other = (DenseDoubleMatrix1D) c;
    }

    double[] elems = this.elements;
    double[] otherElems = other.elements;
    if (elements == null || otherElems == null) {
      throw new IllegalStateException();
    }
    int s = this.stride;
    int ys = other.stride;

    int index = index(0);
    int otherIndex = other.index(0);
    for (int k = size; --k >= 0;) {
      elems[index] = otherElems[otherIndex];
      index += s;
      otherIndex += ys;
    }
    return this;
  }

  
  @Override
  public DoubleMatrix1D assign(DoubleMatrix1D y, DoubleDoubleFunction function) {
    // overriden for performance only
    if (!(y instanceof DenseDoubleMatrix1D)) {
      return super.assign(y, function);
    }
    DenseDoubleMatrix1D other = (DenseDoubleMatrix1D) y;
    checkSize(y);
    double[] elems = this.elements;
    double[] otherElems = other.elements;
    if (elems == null || otherElems == null) {
      throw new IllegalStateException();
    }
    int s = this.stride;
    int ys = other.stride;

    int index = index(0);
    int otherIndex = other.index(0);

    // specialized for speed
    if (function == Functions.MULT) {  // x[i] = x[i] * y[i]
      for (int k = size; --k >= 0;) {
        elems[index] *= otherElems[otherIndex];
        index += s;
        otherIndex += ys;
      }
    } else if (function == Functions.DIV) { // x[i] = x[i] / y[i]
      for (int k = size; --k >= 0;) {
        elems[index] /= otherElems[otherIndex];
        index += s;
        otherIndex += ys;
      }
    } else if (function instanceof PlusMult) {
      double multiplicator = ((PlusMult) function).getMultiplicator();
      if (multiplicator == 0) { // x[i] = x[i] + 0*y[i]
        return this;
      } else if (multiplicator == 1) { // x[i] = x[i] + y[i]
        for (int k = size; --k >= 0;) {
          elems[index] += otherElems[otherIndex];
          index += s;
          otherIndex += ys;
        }
      } else if (multiplicator == -1) { // x[i] = x[i] - y[i]
        for (int k = size; --k >= 0;) {
          elems[index] -= otherElems[otherIndex];
          index += s;
          otherIndex += ys;
        }
      } else { // the general case x[i] = x[i] + mult*y[i]
        for (int k = size; --k >= 0;) {
          elems[index] += multiplicator * otherElems[otherIndex];
          index += s;
          otherIndex += ys;
        }
      }
    } else { // the general case x[i] = f(x[i],y[i])
      for (int k = size; --k >= 0;) {
        elems[index] = function.apply(elems[index], otherElems[otherIndex]);
        index += s;
        otherIndex += ys;
      }
    }
    return this;
  }

  
  @Override
  protected int cardinality(int maxCardinality) {
    int cardinality = 0;
    int index = index(0);
    int s = this.stride;
    double[] elems = this.elements;
    int i = size;
    while (--i >= 0 && cardinality < maxCardinality) {
      if (elems[index] != 0) {
        cardinality++;
      }
      index += s;
    }
    return cardinality;
  }

  
  @Override
  public double getQuick(int index) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //return elements[index(index)];
    // manually inlined:
    return elements[zero + index * stride];
  }

  
  @Override
  protected boolean haveSharedCellsRaw(DoubleMatrix1D other) {
    if (other instanceof SelectedDenseDoubleMatrix1D) {
      SelectedDenseDoubleMatrix1D otherMatrix = (SelectedDenseDoubleMatrix1D) other;
      return this.elements == otherMatrix.elements;
    }
    if (other instanceof DenseDoubleMatrix1D) {
      DenseDoubleMatrix1D otherMatrix = (DenseDoubleMatrix1D) other;
      return this.elements == otherMatrix.elements;
    }
    return false;
  }

  
  @Override
  protected int index(int rank) {
    // overriden for manual inlining only
    //return _offset(_rank(rank));
    return zero + rank * stride;
  }

  
  @Override
  public DoubleMatrix1D like(int size) {
    return new DenseDoubleMatrix1D(size);
  }

  
  @Override
  public DoubleMatrix2D like2D(int rows, int columns) {
    return new DenseDoubleMatrix2D(rows, columns);
  }

  
  @Override
  public void setQuick(int index, double value) {
    //if (debug) if (index<0 || index>=size) checkIndex(index);
    //elements[index(index)] = value;
    // manually inlined:
    elements[zero + index * stride] = value;
  }

  
  @Override
  public void swap(DoubleMatrix1D other) {
    // overriden for performance only
    if (!(other instanceof DenseDoubleMatrix1D)) {
      super.swap(other);
    }
    DenseDoubleMatrix1D y = (DenseDoubleMatrix1D) other;
    if (y == this) {
      return;
    }
    checkSize(y);

    double[] elems = this.elements;
    double[] otherElems = y.elements;
    if (elements == null || otherElems == null) {
      throw new IllegalStateException();
    }
    int s = this.stride;
    int ys = y.stride;

    int index = index(0);
    int otherIndex = y.index(0);
    for (int k = size; --k >= 0;) {
      double tmp = elems[index];
      elems[index] = otherElems[otherIndex];
      otherElems[otherIndex] = tmp;
      index += s;
      otherIndex += ys;
    }
  }

  
  @Override
  public void toArray(double[] values) {
    if (values.length < size) {
      throw new IllegalArgumentException("values too small");
    }
    if (this.isNoView) {
      System.arraycopy(this.elements, 0, values, 0, this.elements.length);
    } else {
      super.toArray(values);
    }
  }

  
  @Override
  protected DoubleMatrix1D viewSelectionLike(int[] offsets) {
    return new SelectedDenseDoubleMatrix1D(this.elements, offsets);
  }

  
  @Override
  public double zDotProduct(DoubleMatrix1D y, int from, int length) {
    if (!(y instanceof DenseDoubleMatrix1D)) {
      return super.zDotProduct(y, from, length);
    }
    DenseDoubleMatrix1D yy = (DenseDoubleMatrix1D) y;

    int tail = from + length;
    if (from < 0 || length < 0) {
      return 0;
    }
    if (size < tail) {
      tail = size;
    }
    if (y.size < tail) {
      tail = y.size;
    }
    int min = tail - from;

    int i = index(from);
    int j = yy.index(from);
    int s = stride;
    int ys = yy.stride;
    double[] elems = this.elements;
    double[] yElems = yy.elements;
    if (elems == null || yElems == null) {
      throw new IllegalStateException();
    }

    /*
    // unoptimized
    for (int k = min; --k >= 0;) {
      sum += elems[i] * yElems[j];
      i += s;
      j += ys;
    }
    */

    // optimized
    // loop unrolling
    i -= s;
    j -= ys;
    double sum = 0;
    for (int k = min / 4; --k >= 0;) {
      sum += elems[i += s] * yElems[j += ys]
          + elems[i += s] * yElems[j += ys]
          + elems[i += s] * yElems[j += ys]
          + elems[i += s] * yElems[j += ys];
    }
    for (int k = min % 4; --k >= 0;) {
      sum += elems[i += s] * yElems[j += ys];
    }
    return sum;
  }

  
  @Override
  public double zSum() {
    int s = stride;
    int i = index(0);
    double[] elems = this.elements;
    if (elems == null) {
      throw new IllegalStateException();
    }
    double sum = 0;
    for (int k = size; --k >= 0;) {
      sum += elems[i];
      i += s;
    }
    return sum;
  }
}
