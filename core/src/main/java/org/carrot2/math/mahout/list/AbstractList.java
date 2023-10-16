/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.math.mahout.list;

import org.carrot2.math.mahout.PersistentObject;

public abstract class AbstractList extends PersistentObject {

  public abstract int size();

  public boolean isEmpty() {
    return size() == 0;
  }

  protected abstract void beforeInsertDummies(int index, int length);

  protected static void checkRange(int index, int theSize) {
    if (index >= theSize || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + theSize);
    }
  }

  protected static void checkRangeFromTo(int from, int to, int theSize) {
    if (to == from - 1) {
      return;
    }
    if (from < 0 || from > to || to >= theSize) {
      throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);
    }
  }

  public void clear() {
    removeFromTo(0, size() - 1);
  }

  public final void mergeSort() {
    mergeSortFromTo(0, size() - 1);
  }

  public abstract void mergeSortFromTo(int from, int to);

  public final void quickSort() {
    quickSortFromTo(0, size() - 1);
  }

  public abstract void quickSortFromTo(int from, int to);

  public void remove(int index) {
    removeFromTo(index, index);
  }

  public abstract void removeFromTo(int fromIndex, int toIndex);

  public abstract void reverse();

  public void setSize(int newSize) {
    if (newSize < 0) {
      throw new IndexOutOfBoundsException("newSize:" + newSize);
    }

    int currentSize = size();
    if (newSize != currentSize) {
      if (newSize > currentSize) {
        beforeInsertDummies(currentSize, newSize - currentSize);
      } else {
        removeFromTo(newSize, currentSize - 1);
      }
    }
  }

  public final void sort() {
    sortFromTo(0, size() - 1);
  }

  public void sortFromTo(int from, int to) {
    quickSortFromTo(from, to);
  }

  public void trimToSize() {}

  @Override
  public int hashCode() {
    throw new RuntimeException("Not implemented.");
  }
}
