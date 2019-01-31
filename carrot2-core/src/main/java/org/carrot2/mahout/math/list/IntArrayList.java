/* Imported from Mahout. */package org.carrot2.mahout.math.list;

import org.carrot2.mahout.math.function.IntProcedure;



public class IntArrayList extends AbstractIntList implements Cloneable {

  
  private int[] elements;

  
  public IntArrayList() {
    this(10);
  }

  
  public IntArrayList(int[] elements) {
    elements(elements);
  }

  
  public IntArrayList(int initialCapacity) {
    this(new int[initialCapacity]);
    setSizeRaw(0);
  }

  
  public void add(int element) {
    // overridden for performance only.
    if (size == elements.length) {
      ensureCapacity(size + 1);
    }
    elements[size++] = element;
  }

  
  public void beforeInsert(int index, int element) {
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
  public int binarySearchFromTo(int key, int from, int to) {
    return org.carrot2.mahout.math.Sorting.binarySearchFromTo(elements, key, from, to);
  }
  
  
  @Override
  public Object clone() {
    // overridden for performance only.
    IntArrayList clone = new IntArrayList(elements.clone());
    clone.setSizeRaw(size);
    return clone;
  }

  
  public IntArrayList copy() {
    return (IntArrayList) clone();
  }

    
  protected void countSortFromTo(int from, int to, int min, int max) {
    if (size == 0) {
      return;
    }
    checkRangeFromTo(from, to, size);

    int width = (int)(max - min + 1);

    int[] counts = new int[width];
    int[] theElements = elements;
    for (int i = from; i <= to;) {
      counts[(theElements[i++] - min)]++;
    }

    int fromIndex = from;
    int val = min;
    for (int i = 0; i < width; i++, val++) {
      int c = counts[i];
      if (c > 0) {
        if (c == 1) {
          theElements[fromIndex++] = val;
        } else {
          int toIndex = fromIndex + c - 1;
          fillFromToWith(fromIndex, toIndex, val);
          fromIndex = toIndex + 1;
        }
      }
    }
  }
  
  
  public int[] elements() {
    return elements;
  }

  
  public AbstractIntList elements(int[] elements) {
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
    if (!(otherObj instanceof IntArrayList)) {
      return super.equals(otherObj);
    }
    if (this == otherObj) {
      return true;
    }
    IntArrayList other = (IntArrayList) otherObj;
    if (size() != other.size()) {
      return false;
    }

    int[] theElements = elements();
    int[] otherElements = other.elements();
    for (int i = size(); --i >= 0;) {
      if (theElements[i] != otherElements[i]) {
        return false;
      }
    }
    return true;
  }

  
  public boolean forEach(IntProcedure procedure) {
    // overridden for performance only.
    int[] theElements = elements;
    int theSize = size;

    for (int i = 0; i < theSize;) {
      if (!procedure.apply(theElements[i++])) {
        return false;
      }
    }
    return true;
  }

  
  public int get(int index) {
    // overridden for performance only.
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return elements[index];
  }

  
  @Override
  public int getQuick(int index) {
    return elements[index];
  }

  
  @Override
  public int indexOfFromTo(int element, int from, int to) {
    // overridden for performance only.
    if (size == 0) {
      return -1;
    }
    checkRangeFromTo(from, to, size);

    int[] theElements = elements;
    for (int i = from; i <= to; i++) {
      if (element == theElements[i]) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  @Override
  public int lastIndexOfFromTo(int element, int from, int to) {
    // overridden for performance only.
    if (size == 0) {
      return -1;
    }
    checkRangeFromTo(from, to, size);

    int[] theElements = elements;
    for (int i = to; i >= from; i--) {
      if (element == theElements[i]) {
        return i;
      } //found
    }
    return -1; //not found
  }

  
  @Override
  public AbstractIntList partFromTo(int from, int to) {
    if (size == 0) {
      return new IntArrayList(0);
    }

    checkRangeFromTo(from, to, size);

    int[] part = new int[to - from + 1];
    System.arraycopy(elements, from, part, 0, to - from + 1);
    return new IntArrayList(part);
  }

  
  @Override
  public boolean removeAll(AbstractIntList other) {
    // overridden for performance only.
    if (!(other instanceof IntArrayList)) {
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
    int[] theElements = elements;
    int mySize = size();

    double N = (double) other.size();
    double M = (double) mySize;
    if ((N + M) * org.carrot2.mahout.collections.Arithmetic.log2(N) < M * N) {
      // it is faster to sort other before searching in it
      IntArrayList sortedList = (IntArrayList) other.clone();
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
  public void replaceFromToWithFrom(int from, int to, AbstractIntList other, int otherFrom) {
    // overridden for performance only.
    if (!(other instanceof IntArrayList)) {
      // slower
      super.replaceFromToWithFrom(from, to, other, otherFrom);
      return;
    }
    int length = to - from + 1;
    if (length > 0) {
      checkRangeFromTo(from, to, size());
      checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());
      System.arraycopy(((IntArrayList) other).elements, otherFrom, elements, from, length);
    }
  }

  
  @Override
  public boolean retainAll(AbstractIntList other) {
    // overridden for performance only.
    if (!(other instanceof IntArrayList)) {
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
    int[] theElements = elements;
    int mySize = size();

    double N = (double) other.size();
    double M = (double) mySize;
    if ((N + M) * org.carrot2.mahout.collections.Arithmetic.log2(N) < M * N) {
      // it is faster to sort other before searching in it
      IntArrayList sortedList = (IntArrayList) other.clone();
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

    int[] theElements = elements;
    for (int i = 0; i < limit;) { //swap
      int tmp = theElements[i];
      theElements[i++] = theElements[j];
      theElements[j--] = tmp;
    }
  }

  
  @Override
  public void set(int index, int element) {
    // overridden for performance only.
    if (index >= size || index < 0) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    elements[index] = element;
  }

  
  @Override
  public void setQuick(int index, int element) {
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
    int min = elements[from];
    int max = elements[from];

    int[] theElements = elements;
    for (int i = from + 1; i <= to;) {
      int elem = theElements[i++];
      if (elem > max) {
        max = elem;
      } else if (elem < min) {
        min = elem;
      }
    }

        // try to figure out which option is fastest.
    double N = (double) to - (double) from + 1.0;
    double quickSortEstimate = N * Math.log(N) / 0.6931471805599453; // O(N*log(N,base=2)) ; ln(2)=0.6931471805599453

    double width = (double) max - (double) min + 1.0;
    double countSortEstimate = Math.max(width, N); // O(Max(width,N))

    int widthThreshold = 10000; // never consider options resulting in outrageous memory allocations.
    if (width < widthThreshold && countSortEstimate < quickSortEstimate) {
      countSortFromTo(from, to, min, max);
    } else {
      quickSortFromTo(from, to);
    }
      }

  
  @Override
  public void trimToSize() {
    elements = org.carrot2.mahout.math.Arrays.trimToCapacity(elements, size());
  }
}
