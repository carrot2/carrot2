/* removed */

/*
Copyright ï¿½ 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package org.carrot2.mahout.math.map;

import org.carrot2.mahout.math.Sorting;
import org.carrot2.mahout.math.Swapper;
import org.carrot2.mahout.math.function.DoubleFunction;
import org.carrot2.mahout.math.function.IntComparator;
import org.carrot2.mahout.math.function.IntDoubleProcedure;
import org.carrot2.mahout.math.function.IntProcedure;
import org.carrot2.mahout.math.list.DoubleArrayList;
import org.carrot2.mahout.math.list.IntArrayList;
import org.carrot2.mahout.math.set.AbstractSet;

public abstract class AbstractIntDoubleMap extends AbstractSet {

  /* removed */
  public boolean containsKey(final int key) {
    return !forEachKey(
        new IntProcedure() {
          @Override
          public boolean apply(int iterKey) {
            return (key != iterKey);
          }
        }
    );
  }

  /* removed */
  public boolean containsValue(final double value) {
    return !forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int iterKey, double iterValue) {
            return (value != iterValue);
          }
        }
    );
  }

  /* removed */
  public AbstractIntDoubleMap copy() {
    return (AbstractIntDoubleMap) clone();
  }

  /* removed */
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof AbstractIntDoubleMap)) {
      return false;
    }
    final AbstractIntDoubleMap other = (AbstractIntDoubleMap) obj;
    if (other.size() != size()) {
      return false;
    }

    return
        forEachPair(
            new IntDoubleProcedure() {
              @Override
              public boolean apply(int key, double value) {
                return other.containsKey(key) && other.get(key) == value;
              }
            }
        )
            &&
            other.forEachPair(
                new IntDoubleProcedure() {
                  @Override
                  public boolean apply(int key, double value) {
                    return containsKey(key) && get(key) == value;
                  }
                }
            );
  }

  /* removed */
  public abstract boolean forEachKey(IntProcedure procedure);

  /* removed */
  public boolean forEachPair(final IntDoubleProcedure procedure) {
    return forEachKey(
        new IntProcedure() {
          @Override
          public boolean apply(int key) {
            return procedure.apply(key, get(key));
          }
        }
    );
  }

  /* removed */
  public abstract double get(int key);

  /* removed */
  public IntArrayList keys() {
    IntArrayList list = new IntArrayList(size());
    keys(list);
    return list;
  }

  /* removed */
  public void keys(final IntArrayList list) {
    list.clear();
    forEachKey(
        new IntProcedure() {
          @Override
          public boolean apply(int key) {
            list.add(key);
            return true;
          }
        }
    );
  }

  /* removed */
  public void keysSortedByValue(IntArrayList keyList) {
    pairsSortedByValue(keyList, new DoubleArrayList(size()));
  }

  /* removed */
  public void pairsMatching(final IntDoubleProcedure condition, 
                           final IntArrayList keyList, 
                           final DoubleArrayList valueList) {
    keyList.clear();
    valueList.clear();

    forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int key, double value) {
            if (condition.apply(key, value)) {
              keyList.add(key);
              valueList.add(value);
            }
            return true;
          }
        }
    );
  }

  /* removed */
  public void pairsSortedByKey(IntArrayList keyList, DoubleArrayList valueList) {
    keys(keyList);
    keyList.sort();
    valueList.setSize(keyList.size());
    for (int i = keyList.size(); --i >= 0;) {
      valueList.setQuick(i, get(keyList.getQuick(i)));
    }
  }

  /* removed */
  public void pairsSortedByValue(IntArrayList keyList, DoubleArrayList valueList) {
    keys(keyList);
    values(valueList);

    final int[] k = keyList.elements();
    final double[] v = valueList.elements();
    Swapper swapper = new Swapper() {
      @Override
      public void swap(int a, int b) {
        double t1 = v[a];
        v[a] = v[b];
        v[b] = t1;
        int t2 = k[a];
        k[a] = k[b];
        k[b] = t2;
      }
    };

    IntComparator comp = new IntComparator() {
      @Override
      public int compare(int a, int b) {
        return v[a] < v[b] ? -1 : v[a] > v[b] ? 1 : (k[a] < k[b] ? -1 : (k[a] == k[b] ? 0 : 1));
      }
    };

    Sorting.quickSort(0, keyList.size(), comp, swapper);
  }

  /* removed */
  public abstract boolean put(int key, double value);

  /* removed */
  public abstract boolean removeKey(int key);

  /* removed */
  public String toString() {
    IntArrayList theKeys = keys();
    //theKeys.sort();

    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = theKeys.size() - 1;
    for (int i = 0; i <= maxIndex; i++) {
      int key = theKeys.get(i);
      buf.append(String.valueOf(key));
      buf.append("->");
      buf.append(String.valueOf(get(key)));
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public String toStringByValue() {
    IntArrayList theKeys = new IntArrayList();
    keysSortedByValue(theKeys);

    StringBuilder buf = new StringBuilder();
    buf.append('[');
    int maxIndex = theKeys.size() - 1;
    for (int i = 0; i <= maxIndex; i++) {
      int key = theKeys.get(i);
      buf.append(String.valueOf(key));
      buf.append("->");
      buf.append(String.valueOf(get(key)));
      if (i < maxIndex) {
        buf.append(", ");
      }
    }
    buf.append(']');
    return buf.toString();
  }

  /* removed */
  public DoubleArrayList values() {
    DoubleArrayList list = new DoubleArrayList(size());
    values(list);
    return list;
  }

  /* removed */
  public void values(final DoubleArrayList list) {
    list.clear();
    forEachKey(
        new IntProcedure() {
          @Override
          public boolean apply(int key) {
            list.add(get(key));
            return true;
          }
        }
    );
  }
  
    /* removed */
  public void assign(final DoubleFunction function) {
    copy().forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int key, double value) {
            put(key, function.apply(value));
            return true;
          }
        }
    );
  }

  /* removed */
  public void assign(AbstractIntDoubleMap other) {
    clear();
    other.forEachPair(
        new IntDoubleProcedure() {
          @Override
          public boolean apply(int key, double value) {
            put(key, value);
            return true;
          }
        }
    );
  }
  
  /* removed */
  public double adjustOrPutValue(int key, double newValue, double incrValue) {
      boolean present = containsKey(key);
      if (present) {
        newValue = (double)(get(key) + incrValue);
        put(key, newValue);
      } else {
        put(key, newValue);
      }
      return newValue;
  }
}
