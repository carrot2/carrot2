/* Imported from Mahout. */package org.carrot2.mahout.math.list;

import org.carrot2.mahout.math.function.DoubleProcedure;



public class DoubleArrayList extends AbstractDoubleList implements Cloneable {

  
  private double[] elements;

  
  public DoubleArrayList() {
    this(10);
  }

  
  public DoubleArrayList(double[] elements) {
    elements(elements);
  }

  
  public DoubleArrayList(int initialCapacity) {
    this(new double[initialCapacity]);
    setSizeRaw(0);
  }

  
  public void add(double element) {
    // overridden for performance only.
    if (size == elements.length) {
      ensureCapacity(size + 1);
    }
    elements[size++] = element;
  }

  
  public void beforeInsert(int index, double element) {
    // overridden for performance only.
    if (size == index) {
      add(element);
      return;
    }
    if (index > size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    ensureCapacity(size + 1);
    System.arraycopy(elements, index, elements, index + 1, size - index);
    elements[index] = element;
    size++;
  }

  
  @Override
  public int binarySearchFromTo(double key, int from, int to) {
    return org.carrot2.mahout.math.Sorting.binarySearchFromTo(elements, key, from, to);
  }
  
  
  @Override
  public Object clone() {
    // overridden for performance only.
    DoubleArrayList clone = new DoubleArrayList(elements.clone());
    clone.setSizeRaw(size);
    return clone;
  }

  
  public DoubleArrayList copy() {
    return (DoubleArrayList) clone();
  }

  
  
  public double[] elements() {
    return elements;
  }

  
  public AbstractDoubleList elements(double[] elements) {
    this.elements = elements;
    this.size = elements.length;
    return this;
  }

  
  public void ensureCapacity(int minCapacity) {
    elements = org.carrot2.mahout.math.Arrays.ensureCapacity(elements, minCapacity);
  }

  
  public boolean equals(Object otherObj) { //delta
    if (otherObj == null) {
      return false;
    }
    // overridden for performance only.
    if (!(otherObj instanceof DoubleArrayList)) {
      return super.equals(otherObj);
    }
    if (this == otherObj) {
      return true;
    }
    DoubleArrayList other = (DoubleArrayList) otherObj;
    if (size() != other.size()) {
      return false;
    }

    double[] theElements = elements();
    double[] otherElements = other.elements();
    for (int i = size(); --i >= 0;) {
      if (theElements[i] != otherElements[i]) {
        return false;
      }
    }
    return true;
  }

  
  public boolean forEach(DoubleProcedure procedure) {
    // overridden for performance only.
    double[] theElements = elements;
    int theSize = size;

    for (int i = 0; i < theSize;) {
      if (!procedure.apply(theElements[i++])) {
        return false;
      }
    }
    return true;
  }

  
  public double get(int index) {
    // overridden for performance only.
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return elements[index];
  }

  
  @Override
  public double getQuick(int index) {
    return elements[index];
  }

  
  @Override
  public int indexOfFromTo(double element, int from, int to) {
    // overridden for performance only.
    if (size == 0) {
      return -1;
    }
    checkRangeFromTo(from, to, size);

    double[] theElements = elements;
    for (int i = from; i <= to; i++) {
      if (element == theElements[i]) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  @Override
  public int lastIndexOfFromTo(double element, int from, int to) {
    // overridden for performance only.
    if (size == 0) {
      return -1;
    }
    checkRangeFromTo(from, to, size);

    double[] theElements = elements;
    for (int i = to; i >= from; i--) {
      if (element == theElements[i]) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  @Override
  public AbstractDoubleList partFromTo(int from, int to) {
    if (size == 0) {
      return new DoubleArrayList(0);
    }

    checkRangeFromTo(from, to, size);

    double[] part = new double[to - from + 1];
    System.arraycopy(elements, from, part, 0, to - from + 1);
    return new DoubleArrayList(part);
  }

  
  @Override
  public boolean removeAll(AbstractDoubleList other) {
    // overridden for performance only.
    if (!(other instanceof DoubleArrayList)) {
      return super.removeAll(other);
    }

    /* There are two possibilities to do the thing
       a) use other.indexOf(...)
       b) sort other, then use other.binarySearch(...)

       Let's try to figure out which one is faster. Let M=size, N=other.size, then
       a) takes O(M*N) steps
       b) takes O(N*logN + M*logN) steps (sorting is O(N*logN) and binarySearch is O(logN))

       Hence, if N*logN + M*logN < M*N, we use b) otherwise we use a).
    */
    if (other.isEmpty()) {
      return false;
    } //nothing to do
    int limit = other.size() - 1;
    int j = 0;
    double[] theElements = elements;
    int mySize = size();

    double N = (double) other.size();
    double M = (double) mySize;
    if ((N + M) * org.carrot2.mahout.collections.Arithmetic.log2(N) < M * N) {
      // it is faster to sort other before searching in it
      DoubleArrayList sortedList = (DoubleArrayList) other.clone();
      sortedList.quickSort();

      for (int i = 0; i < mySize; i++) {
        if (sortedList.binarySearchFromTo(theElements[i], 0, limit) < 0) {
          theElements[j++] = theElements[i];
        }
      }
    } else {
      // it is faster to search in other without sorting
      for (int i = 0; i < mySize; i++) {
        if (other.indexOfFromTo(theElements[i], 0, limit) < 0) {
          theElements[j++] = theElements[i];
        }
      }
    }

    boolean modified = (j != mySize);
    setSize(j);
    return modified;
  }

  
  @Override
  public void replaceFromToWithFrom(int from, int to, AbstractDoubleList other, int otherFrom) {
    // overridden for performance only.
    if (!(other instanceof DoubleArrayList)) {
      // slower
      super.replaceFromToWithFrom(from, to, other, otherFrom);
      return;
    }
    int length = to - from + 1;
    if (length > 0) {
      checkRangeFromTo(from, to, size());
      checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());
      System.arraycopy(((DoubleArrayList) other).elements, otherFrom, elements, from, length);
    }
  }

  
  @Override
  public boolean retainAll(AbstractDoubleList other) {
    // overridden for performance only.
    if (!(other instanceof DoubleArrayList)) {
      return super.retainAll(other);
    }

    /* There are two possibilities to do the thing
       a) use other.indexOf(...)
       b) sort other, then use other.binarySearch(...)

       Let's try to figure out which one is faster. Let M=size, N=other.size, then
       a) takes O(M*N) steps
       b) takes O(N*logN + M*logN) steps (sorting is O(N*logN) and binarySearch is O(logN))

       Hence, if N*logN + M*logN < M*N, we use b) otherwise we use a).
    */
    int limit = other.size() - 1;
    int j = 0;
    double[] theElements = elements;
    int mySize = size();

    double N = (double) other.size();
    double M = (double) mySize;
    if ((N + M) * org.carrot2.mahout.collections.Arithmetic.log2(N) < M * N) {
      // it is faster to sort other before searching in it
      DoubleArrayList sortedList = (DoubleArrayList) other.clone();
      sortedList.quickSort();

      for (int i = 0; i < mySize; i++) {
        if (sortedList.binarySearchFromTo(theElements[i], 0, limit) >= 0) {
          theElements[j++] = theElements[i];
        }
      }
    } else {
      // it is faster to search in other without sorting
      for (int i = 0; i < mySize; i++) {
        if (other.indexOfFromTo(theElements[i], 0, limit) >= 0) {
          theElements[j++] = theElements[i];
        }
      }
    }

    boolean modified = (j != mySize);
    setSize(j);
    return modified;
  }

  
  @Override
  public void reverse() {
    // overridden for performance only.
    int limit = size / 2;
    int j = size - 1;

    double[] theElements = elements;
    for (int i = 0; i < limit;) { //swap
      double tmp = theElements[i];
      theElements[i++] = theElements[j];
      theElements[j--] = tmp;
    }
  }

  
  @Override
  public void set(int index, double element) {
    // overridden for performance only.
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    elements[index] = element;
  }

  
  @Override
  public void setQuick(int index, double element) {
    elements[index] = element;
  }

  

  
  @Override
  public void sortFromTo(int from, int to) {
    /*
    * Computes min and max and decides on this basis.
    * In practice the additional overhead is very small compared to the potential gains.
    */

    if (size == 0) {
      return;
    }
    checkRangeFromTo(from, to, size);

    // determine minimum and maximum.
    double min = elements[from];
    double max = elements[from];

    double[] theElements = elements;
    for (int i = from + 1; i <= to;) {
      double elem = theElements[i++];
      if (elem > max) {
        max = elem;
      } else if (elem < min) {
        min = elem;
      }
    }

        quickSortFromTo(from, to);
      }

  
  @Override
  public void trimToSize() {
    elements = org.carrot2.mahout.math.Arrays.trimToCapacity(elements, size());
  }
}
