/* Imported from Mahout. */package org.carrot2.mahout.math.list;
//CHECKSTYLE:OFF
//CHECKSTYLE:ON

import org.carrot2.mahout.math.Sorting;
import org.carrot2.mahout.math.buffer.IntBufferConsumer;
import org.carrot2.mahout.math.function.IntComparator;
import org.carrot2.mahout.math.function.IntProcedure;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractIntList extends AbstractList implements IntBufferConsumer, Cloneable {

  
  protected int size;

  
  public void add(int element) {
    beforeInsert(size, element);
  }

  
  public void addAllOf(AbstractIntList other) {
    addAllOfFromTo(other, 0, other.size() - 1);
  }

  
  public void addAllOfFromTo(AbstractIntList other, int from, int to) {
    beforeInsertAllOfFromTo(size, other, from, to);
  }
  
  
  @Override
  public void addAllOf(IntArrayList other) {
	addAllOfFromTo(other, 0, other.size() - 1);
  }

  
  public void beforeInsert(int index, int element) {
    beforeInsertDummies(index, 1);
    set(index, element);
  }

  
  public void beforeInsertAllOfFromTo(int index, AbstractIntList other, int from, int to) {
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
  
  public int binarySearch(int key) {
    return this.binarySearchFromTo(key, 0, size - 1);
  }

  
  public int binarySearchFromTo(int key, int from, int to) {
    int low = from;
    int high = to;
    while (low <= high) {
      int mid = (low + high) / 2;
      int midVal = get(mid);

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

  
  public boolean contains(int elem) {
    return indexOfFromTo(elem, 0, size - 1) >= 0;
  }

  
  public void delete(int element) {
    int index = indexOfFromTo(element, 0, size - 1);
    if (index >= 0) {
      remove(index);
    }
  }

  
  public int[] elements() {
    int[] myElements = new int[size];
    for (int i = size; --i >= 0;) {
      myElements[i] = getQuick(i);
    }
    return myElements;
  }

  
  public AbstractIntList elements(int[] elements) {
    clear();
    addAllOfFromTo(new IntArrayList(elements), 0, elements.length - 1);
    return this;
  }

  
  public abstract void ensureCapacity(int minCapacity);

  
  public boolean equals(Object otherObj) { //delta
    if (otherObj == null) {
      return false;
    }
    if (!(otherObj instanceof AbstractIntList)) {
      return false;
    }
    if (this == otherObj) {
      return true;
    }
    AbstractIntList other = (AbstractIntList) otherObj;
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

  
  public void fillFromToWith(int from, int to, int val) {
    checkRangeFromTo(from, to, this.size);
    for (int i = from; i <= to;) {
      setQuick(i++, val);
    }
  }

  
  public boolean forEach(IntProcedure procedure) {
    for (int i = 0; i < size;) {
      if (!procedure.apply(get(i++))) {
        return false;
      }
    }
    return true;
  }

  
  public int get(int index) {
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return getQuick(index);
  }

  
  protected abstract int getQuick(int index);

  
  public int indexOf(int element) { //delta
    return indexOfFromTo(element, 0, size - 1);
  }

  
  public int indexOfFromTo(int element, int from, int to) {
    checkRangeFromTo(from, to, size);

    for (int i = from; i <= to; i++) {
      if (element == getQuick(i)) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  public int lastIndexOf(int element) {
    return lastIndexOfFromTo(element, 0, size - 1);
  }

  
  public int lastIndexOfFromTo(int element, int from, int to) {
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

    int[] myElements = elements();
    Sorting.mergeSort(myElements, from, to + 1);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public void mergeSortFromTo(int from, int to, IntComparator c) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    int[] myElements = elements();
    Sorting.mergeSort(myElements, from, to + 1, c);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public AbstractIntList partFromTo(int from, int to) {
    checkRangeFromTo(from, to, size);

    int length = to - from + 1;
    IntArrayList part = new IntArrayList(length);
    part.addAllOfFromTo(this, from, to);
    return part;
  }
  
  
  @Override
  public void quickSortFromTo(int from, int to) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    int[] myElements = elements();
    java.util.Arrays.sort(myElements, from, to + 1);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public void quickSortFromTo(int from, int to, IntComparator c) {
    int mySize = size();
    checkRangeFromTo(from, to, mySize);

    int[] myElements = elements();
    Sorting.quickSort(myElements, from, to + 1, c);
    elements(myElements);
    setSizeRaw(mySize);
  }

  
  public boolean removeAll(AbstractIntList other) {
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

  
  public void replaceFromToWithFrom(int from, int to, AbstractIntList other, int otherFrom) {
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
  public void replaceFromToWithFromTo(int from, int to, AbstractIntList other, int otherFrom, int otherTo) {
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
  
  
  public boolean retainAll(AbstractIntList other) {
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
      int tmp = getQuick(i);
      setQuick(i++, getQuick(j));
      setQuick(j--, tmp);
    }
  }

  
  public void set(int index, int element) {
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    setQuick(index, element);
  }

  
  protected abstract void setQuick(int index, int element);

  
  protected void setSizeRaw(int newSize) {
    size = newSize;
  }

  
  @Override
  public int size() {
    return size;
  }

  
  public AbstractIntList times(int times) {
    AbstractIntList newList = new IntArrayList(times * size());
    for (int i = times; --i >= 0;) {
      newList.addAllOfFromTo(this, 0, size() - 1);
    }
    return newList;
  }

  
  public List<Integer> toList() {
    int mySize = size();
    List<Integer> list = new ArrayList<Integer>(mySize);
    for (int i = 0; i < mySize; i++) {
      list.add(get(i));
    }
    return list;
  }
  
  public int[] toArray(int[] values) {
   int mySize = size();
   int[] myElements;
   if (values.length >= mySize) {
     myElements = values;
   } else {
     myElements = new int[mySize];
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
