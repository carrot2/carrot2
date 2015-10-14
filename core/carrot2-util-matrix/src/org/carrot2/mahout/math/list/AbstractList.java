/* removed */
 /*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.list;

import org.carrot2.mahout.math.PersistentObject;

/* removed */
public abstract class AbstractList extends PersistentObject {
  
  public abstract int size();
  
  public boolean isEmpty() {
    return size() == 0;
  }

  /* removed */
  protected abstract void beforeInsertDummies(int index, int length);

  /* removed */
  protected static void checkRange(int index, int theSize) {
    if (index >= theSize || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + theSize);
    }
  }

  /* removed */
  protected static void checkRangeFromTo(int from, int to, int theSize) {
    if (to == from - 1) {
      return;
    }
    if (from < 0 || from > to || to >= theSize) {
      throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);
    }
  }

  /* removed */
  public void clear() {
    removeFromTo(0, size() - 1);
  }

  /* removed */
  public final void mergeSort() {
    mergeSortFromTo(0, size() - 1);
  }

  /* removed */
  public abstract void mergeSortFromTo(int from, int to);

  /* removed */
  public final void quickSort() {
    quickSortFromTo(0, size() - 1);
  }

  /* removed */
  public abstract void quickSortFromTo(int from, int to);

  /* removed */
  public void remove(int index) {
    removeFromTo(index, index);
  }

  /* removed */
  public abstract void removeFromTo(int fromIndex, int toIndex);

  /* removed */
  public abstract void reverse();

  /* removed */
  public void setSize(int newSize) {
    if (newSize < 0) {
      throw new IndexOutOfBoundsException("newSize:" + newSize);
    }

    int currentSize = size();
    if (newSize != currentSize) {
      if (newSize > currentSize) {
        beforeInsertDummies(currentSize, newSize - currentSize);
      } else if (newSize < currentSize) {
        removeFromTo(newSize, currentSize - 1);
      }
    }
  }

  /* removed */
  public final void sort() {
    sortFromTo(0, size() - 1);
  }

  /* removed */
  public void sortFromTo(int from, int to) {
    quickSortFromTo(from, to);
  }

  /* removed */
  public void trimToSize() {
  }
}
