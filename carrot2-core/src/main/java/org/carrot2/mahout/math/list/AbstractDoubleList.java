/* Imported from Mahout. */package org.carrot2.mahout.math.list;
//CHECKSTYLE:OFF
//CHECKSTYLE:ON

import org.carrot2.mahout.math.Sorting;
import org.carrot2.mahout.math.buffer.DoubleBufferConsumer;
import org.carrot2.mahout.math.function.DoubleComparator;
import org.carrot2.mahout.math.function.DoubleProcedure;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractDoubleList extends AbstractList implements DoubleBufferConsumer, Cloneable {

  
  protected int size;

  
  public void add(double element) {
    beforeInsert(size, element);
  }

  
  public void addAllOf(AbstractDoubleList other) {
    addAllOfFromTo(other, 0, other.size() - 1);
  }

  
  public void addAllOfFromTo(AbstractDoubleList other, int from, int to) {
    beforeInsertAllOfFromTo(size, other, from, to);
  }
  
  
  @Override
  public void addAllOf(DoubleArrayList other) {
	addAllOfFromTo(other, 0, other.size() - 1);
  }

  
  public void beforeInsert(int index, double element) {
    beforeInsertDummies(index, 1);
    set(index, element);
  }

  
  public void beforeInsertAllOfFromTo(int index, AbstractDoubleList other, int from, int to) {
    int length = to - from + 1;
    this.beforeInsertDummies(index, length);
    this.replaceFromToWithFrom(index, index + length - 1, other, from);
  }

  
  @Override
  protected void beforeInsertDummies(int index, int length) {
    if (index > size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    if (length > 0) {
      ensureCapacity(size + length);
      setSizeRaw(size + length);
      replaceFromToWithFrom(index + length, size - 1, this, index);
    }
  }
  
  public int binarySearch(double key) {
    return this.binarySearchFromTo(key, 0, size - 1);
  }

  
  public int binarySearchFromTo(double key, int from, int to) {
    int low = from;
    int high = to;
    while (low <= high) {
      int mid = (low + high) / 2;
      double midVal = get(mid);

      if (midVal < key) {
        low = mid + 1;
      } else if (midVal > key) {
        high = mid - 1;
      } else {
        return mid;
      } // key found
    }
    return -(low + 1);  // key not found.
  }

  
  @Override
  public Object clone() {
    return partFromTo(0, size - 1);
  }

  
  public boolean contains(double elem) {
    return indexOfFromTo(elem, 0, size - 1) >= 0;
  }

  
  public void delete(double element) {
    int index = indexOfFromTo(element, 0, size - 1);
    if (index >= 0) {
      remove(index);
    }
  }

  
  public double[] elements() {
    double[] myElements = new double[size];
    for (int i = size; --i >= 0;) {
      myElements[i] = getQuick(i);
    }
    return myElements;
  }

  
  public AbstractDoubleList elements(double[] elements) {
    clear();
    addAllOfFromTo(new DoubleArrayList(elements), 0, elements.length - 1);
    return this;
  }

  
  public abstract void ensureCapacity(int minCapacity);

  
  public boolean equals(Object otherObj) { //delta
    if (otherObj == null) {
      return false;
    }
    if (!(otherObj instanceof AbstractDoubleList)) {
      return false;
    }
    if (this == otherObj) {
      return true;
    }
    AbstractDoubleList other = (AbstractDoubleList) otherObj;
    if (size() != other.size()) {
      return false;
    }

    for (int i = size(); --i >= 0;) {
      if (getQuick(i) != other.getQuick(i)) {
        return false;
      }
    }
    return true;
  }

  
  public void fillFromToWith(int from, int to, double val) {
    checkRangeFromTo(from, to, this.size);
    for (int i = from; i <= to;) {
      setQuick(i++, val);
    }
  }

  
  public boolean forEach(DoubleProcedure procedure) {
    for (int i = 0; i < size;) {
      if (!procedure.apply(get(i++))) {
        return false;
      }
    }
    return true;
  }

  
  public double get(int index) {
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return getQuick(index);
  }

  
  protected abstract double getQuick(int index);

  
  public int indexOf(double element) { //delta
    return indexOfFromTo(element, 0, size - 1);
  }

  
  public int indexOfFromTo(double element, int from, int to) {
    checkRangeFromTo(from, to, size);

    for (int i = from; i <= to; i++) {
      if (element == getQuick(i)) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  public int lastIndexOf(double element) {
    return lastIndexOfFromTo(element, 0, size - 1);
  }

  
  public int lastIndexOfFromTo(double element, int from, int to) {
    checkRangeFromTo(from, to, size());

    for (int i = to; i >= from; i--) {
      if (element == getQuick(i)) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  @Override
  public void mergeSortFromTo(int from, int to) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    double[] myElements = elements();
    Sorting.mergeSort(myElements, from, to + 1);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public void mergeSortFromTo(int from, int to, DoubleComparator c) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    double[] myElements = elements();
    Sorting.mergeSort(myElements, from, to + 1, c);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public AbstractDoubleList partFromTo(int from, int to) {
    checkRangeFromTo(from, to, size);

    int length = to - from + 1;
    DoubleArrayList part = new DoubleArrayList(length);
    part.addAllOfFromTo(this, from, to);
    return part;
  }
  
  
  @Override
  public void quickSortFromTo(int from, int to) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    double[] myElements = elements();
    java.util.Arrays.sort(myElements, from, to + 1);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public void quickSortFromTo(int from, int to, DoubleComparator c) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    double[] myElements = elements();
    Sorting.quickSort(myElements, from, to + 1, c);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public boolean removeAll(AbstractDoubleList other) {
    if (other.isEmpty()) {
      return false;
    } //nothing to do
    int limit = other.size() - 1;
    int j = 0;

    for (int i = 0; i < size; i++) {
      if (other.indexOfFromTo(getQuick(i), 0, limit) < 0) {
        setQuick(j++, getQuick(i));
      }
    }

    boolean modified = (j != size);
    setSize(j);
    return modified;
  }

  
  @Override
  public void removeFromTo(int from, int to) {
    checkRangeFromTo(from, to, size);
    int numMoved = size - to - 1;
    if (numMoved > 0) {
      replaceFromToWithFrom(from, from - 1 + numMoved, this, to + 1);
      //fillFromToWith(from+numMoved, size-1, 0.0f); //delta
    }
    int width = to - from + 1;
    if (width > 0) {
      setSizeRaw(size - width);
    }
  }

  
  public void replaceFromToWithFrom(int from, int to, AbstractDoubleList other, int otherFrom) {
    int length = to - from + 1;
    if (length > 0) {
      checkRangeFromTo(from, to, size());
      checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());

      // unambiguous copy (it may hold other==this)
      if (from <= otherFrom) {
        while (--length >= 0) {
          setQuick(from++, other.getQuick(otherFrom++));
        }
      } else {
        int otherTo = otherFrom + length - 1;
        while (--length >= 0) {
          setQuick(to--, other.getQuick(otherTo--));
        }
      }
    }
  }

  public void replaceFromToWithFromTo(int from, int to, AbstractDoubleList other, int otherFrom, int otherTo) {
    if (otherFrom > otherTo) {
      throw new IndexOutOfBoundsException("otherFrom: " + otherFrom + ", otherTo: " + otherTo);
    }

    if (this == other && to - from != otherTo - otherFrom) { // avoid stumbling over my own feet
      replaceFromToWithFromTo(from, to, partFromTo(otherFrom, otherTo), 0, otherTo - otherFrom);
      return;
    }

    int length = otherTo - otherFrom + 1;
    int diff = length;
    int theLast = from - 1;

    if (to >= from) {
      diff -= (to - from + 1);
      theLast = to;
    }

    if (diff > 0) {
      beforeInsertDummies(theLast + 1, diff);
    } else {
      if (diff < 0) {
        removeFromTo(theLast + diff, theLast - 1);
      }
    }

    if (length > 0) {
      replaceFromToWithFrom(from, from + length - 1, other, otherFrom);
    }
  }
  
  
  public boolean retainAll(AbstractDoubleList other) {
    if (other.isEmpty()) {
      if (size == 0) {
        return false;
      }
      setSize(0);
      return true;
    }

    int limit = other.size() - 1;
    int j = 0;
    for (int i = 0; i < size; i++) {
      if (other.indexOfFromTo(getQuick(i), 0, limit) >= 0) {
        setQuick(j++, getQuick(i));
      }
    }

    boolean modified = (j != size);
    setSize(j);
    return modified;
  }
  
  
  @Override
  public void reverse() {
    int limit = size() / 2;
    int j = size() - 1;

    for (int i = 0; i < limit;) { //swap
      double tmp = getQuick(i);
      setQuick(i++, getQuick(j));
      setQuick(j--, tmp);
    }
  }

  
  public void set(int index, double element) {
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    setQuick(index, element);
  }

  
  protected abstract void setQuick(int index, double element);

  
  protected void setSizeRaw(int newSize) {
    size = newSize;
  }

  
  @Override
  public int size() {
    return size;
  }

  
  public AbstractDoubleList times(int times) {
    AbstractDoubleList newList = new DoubleArrayList(times * size());
    for (int i = times; --i >= 0;) {
      newList.addAllOfFromTo(this, 0, size() - 1);
    }
    return newList;
  }

  
  public List<Double> toList() {
    int mySize = size();
    List<Double> list = new ArrayList<Double>(mySize);
    for (int i = 0; i < mySize; i++) {
      list.add(get(i));
    }
    return list;
  }
  
  public double[] toArray(double[] values) {
   int mySize = size();
   double[] myElements;
   if (values.length >= mySize) {
     myElements = values;
   } else {
     myElements = new double[mySize];
   }
   for (int i = size; --i >= 0;) {
      myElements[i] = getQuick(i);
    }
    return myElements;
  }

  
  public String toString() {
    return org.carrot2.mahout.math.Arrays.toString(partFromTo(0, size() - 1).elements());
  }
}
