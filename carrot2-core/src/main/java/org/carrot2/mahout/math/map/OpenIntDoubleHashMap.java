/* Imported from Mahout. */package org.carrot2.mahout.math.map;

import java.util.Arrays;

import org.carrot2.mahout.math.function.IntDoubleProcedure;
import org.carrot2.mahout.math.function.IntProcedure;
import org.carrot2.mahout.math.list.DoubleArrayList;
import org.carrot2.mahout.math.list.IntArrayList;


public class OpenIntDoubleHashMap extends AbstractIntDoubleMap {
  protected static final byte FREE = 0;
  protected static final byte FULL = 1;
  protected static final byte REMOVED = 2;
  protected static final int NO_KEY_VALUE = 0;

  
  protected int[] table;

  
  protected double[] values;

  
  protected byte[] state;

  
  protected int freeEntries;


  
  public OpenIntDoubleHashMap() {
    this(defaultCapacity);
  }

  
  public OpenIntDoubleHashMap(int initialCapacity) {
    this(initialCapacity, defaultMinLoadFactor, defaultMaxLoadFactor);
  }

  
  public OpenIntDoubleHashMap(int initialCapacity, double minLoadFactor, double maxLoadFactor) {
    setUp(initialCapacity, minLoadFactor, maxLoadFactor);
  }

  
  @Override
  public void clear() {
    Arrays.fill(this.state, FREE);
    distinct = 0;
    freeEntries = table.length; // delta
    trimToSize();
  }

  
  @Override
  public Object clone() {
    OpenIntDoubleHashMap copy = (OpenIntDoubleHashMap) super.clone();
    copy.table = copy.table.clone();
    copy.values = copy.values.clone();
    copy.state = copy.state.clone();
    return copy;
  }

  
  @Override
  public boolean containsKey(int key) {
    return indexOfKey(key) >= 0;
  }

  
  @Override
  public boolean containsValue(double value) {
    return indexOfValue(value) >= 0;
  }

  
  @Override
  public void ensureCapacity(int minCapacity) {
    if (table.length < minCapacity) {
      int newCapacity = nextPrime(minCapacity);
      rehash(newCapacity);
    }
  }

  
  @Override
  public boolean forEachKey(IntProcedure procedure) {
    for (int i = table.length; i-- > 0;) {
      if (state[i] == FULL) {
        if (!procedure.apply(table[i])) {
          return false;
        }
      }
    }
    return true;
  }

  
  @Override
  public boolean forEachPair(IntDoubleProcedure procedure) {
    for (int i = table.length; i-- > 0;) {
      if (state[i] == FULL) {
        if (!procedure.apply(table[i], values[i])) {
          return false;
        }
      }
    }
    return true;
  }

  
  @Override
  public double get(int key) {
    final int i = indexOfKey(key);
    if (i < 0) {
      return 0;
    } //not contained
    return values[i];
  }

  
  protected int indexOfInsertion(int key) {
    final int length = table.length;

    final int hash = HashFunctions.hash(key) & 0x7FFFFFFF;
    int i = hash % length;
    int decrement = hash % (length - 2); // double hashing, see http://www.eece.unm.edu/faculty/heileman/hash/node4.html
    //int decrement = (hash / length) % length;
    if (decrement == 0) {
      decrement = 1;
    }

    // stop if we find a removed or free slot, or if we find the key itself
    // do NOT skip over removed slots (yes, open addressing is like that...)
    while (state[i] == FULL && table[i] != key) {
      i -= decrement;
      //hashCollisions++;
      if (i < 0) {
        i += length;
      }
    }

    if (state[i] == REMOVED) {
      // stop if we find a free slot, or if we find the key itself.
      // do skip over removed slots (yes, open addressing is like that...)
      // assertion: there is at least one FREE slot.
      final int j = i;
      while (state[i] != FREE && (state[i] == REMOVED || table[i] != key)) {
        i -= decrement;
        //hashCollisions++;
        if (i < 0) {
          i += length;
        }
      }
      if (state[i] == FREE) {
        i = j;
      }
    }


    if (state[i] == FULL) {
      // key already contained at slot i.
      // return a negative number identifying the slot.
      return -i - 1;
    }
    // not already contained, should be inserted at slot i.
    // return a number >= 0 identifying the slot.
    return i;
  }

  
  protected int indexOfKey(int key) {
    final int length = table.length;

    final int hash = HashFunctions.hash(key) & 0x7FFFFFFF;
    int i = hash % length;
    int decrement = hash % (length - 2); // double hashing, see http://www.eece.unm.edu/faculty/heileman/hash/node4.html
    //int decrement = (hash / length) % length;
    if (decrement == 0) {
      decrement = 1;
    }

    // stop if we find a free slot, or if we find the key itself.
    // do skip over removed slots (yes, open addressing is like that...)
    while (state[i] != FREE && (state[i] == REMOVED || table[i] != key)) {
      i -= decrement;
      //hashCollisions++;
      if (i < 0) {
        i += length;
      }
    }

    if (state[i] == FREE) {
      return -1;
    } // not found
    return i; //found, return index where key is contained
  }

  
  protected int indexOfValue(double value) {
    double[] val = values;
    byte[] stat = state;

    for (int i = stat.length; --i >= 0;) {
      if (stat[i] == FULL && val[i] == value) {
        return i;
      }
    }

    return -1; // not found
  }

  
  @Override
  public void keys(IntArrayList list) {
    list.setSize(distinct);
    int [] elements = list.elements();

    int j = 0;
    for (int i = table.length; i-- > 0;) {
      if (state[i] == FULL) {
        elements[j++] = table[i];
      }
    }
  }

  
  @Override
  public void pairsMatching(IntDoubleProcedure condition, 
                            IntArrayList keyList, 
                            DoubleArrayList valueList) {
    keyList.clear();
    valueList.clear();

    for (int i = table.length; i-- > 0;) {
      if (state[i] == FULL && condition.apply(table[i], values[i])) {
        keyList.add(table[i]);
        valueList.add(values[i]);
      }
    }
  }

  
  @Override
  public boolean put(int key, double value) {
    int i = indexOfInsertion(key);
    if (i < 0) { //already contained
      i = -i - 1;
      this.values[i] = value;
      return false;
    }

    if (this.distinct > this.highWaterMark) {
      int newCapacity = chooseGrowCapacity(this.distinct + 1, this.minLoadFactor, this.maxLoadFactor);
      rehash(newCapacity);
      return put(key, value);
    }

    this.table[i] = key;
    this.values[i] = value;
    if (this.state[i] == FREE) {
      this.freeEntries--;
    }
    this.state[i] = FULL;
    this.distinct++;

    if (this.freeEntries < 1) { //delta
      int newCapacity = chooseGrowCapacity(this.distinct + 1, this.minLoadFactor, this.maxLoadFactor);
      rehash(newCapacity);
    }

    return true;
  }

  @Override
  public double adjustOrPutValue(int key, double newValue, double incrValue) {
    int i = indexOfInsertion(key);
    if (i < 0) { //already contained
      i = -i - 1;
      this.values[i] += incrValue;
      return this.values[i];
    } else {
        put(key, newValue);
        return newValue;
    }
 }
  
  
  protected void rehash(int newCapacity) {
    int oldCapacity = table.length;
    //if (oldCapacity == newCapacity) return;

    int[] oldTable = table;
    double[] oldValues = values;
    byte[] oldState = state;

    this.table = new int[newCapacity];
    this.values = new double[newCapacity];
    this.state = new byte[newCapacity];

    this.lowWaterMark = chooseLowWaterMark(newCapacity, this.minLoadFactor);
    this.highWaterMark = chooseHighWaterMark(newCapacity, this.maxLoadFactor);

    this.freeEntries = newCapacity - this.distinct; // delta

    for (int i = oldCapacity; i-- > 0;) {
      if (oldState[i] == FULL) {
        int element = oldTable[i];
        int index = indexOfInsertion(element);
        this.table[index] = element;
        this.values[index] = oldValues[i];
        this.state[index] = FULL;
      }
    }
  }

  
  @Override
  public boolean removeKey(int key) {
    int i = indexOfKey(key);
    if (i < 0) {
      return false;
    } // key not contained

    this.state[i] = REMOVED;
    //this.values[i]=0; // delta
    this.distinct--;

    if (this.distinct < this.lowWaterMark) {
      int newCapacity = chooseShrinkCapacity(this.distinct, this.minLoadFactor, this.maxLoadFactor);
      rehash(newCapacity);
    }

    return true;
  }

  
  @Override
  protected void setUp(int initialCapacity, double minLoadFactor, double maxLoadFactor) {
    int capacity = initialCapacity;
    super.setUp(capacity, minLoadFactor, maxLoadFactor);
    capacity = nextPrime(capacity);
    if (capacity == 0) {
      capacity = 1;
    } // open addressing needs at least one FREE slot at any time.

    this.table = new int[capacity];
    this.values = new double[capacity];
    this.state = new byte[capacity];

    // memory will be exhausted long before this pathological case happens, anyway.
    this.minLoadFactor = minLoadFactor;
    if (capacity == PrimeFinder.largestPrime) {
      this.maxLoadFactor = 1.0;
    } else {
      this.maxLoadFactor = maxLoadFactor;
    }

    this.distinct = 0;
    this.freeEntries = capacity; // delta

    // lowWaterMark will be established upon first expansion.
    // establishing it now (upon instance construction) would immediately make the table shrink upon first put(...).
    // After all the idea of an "initialCapacity" implies violating lowWaterMarks when an object is young.
    // See ensureCapacity(...)
    this.lowWaterMark = 0;
    this.highWaterMark = chooseHighWaterMark(capacity, this.maxLoadFactor);
  }

  
  @Override
  public void trimToSize() {
    // * 1.2 because open addressing's performance exponentially degrades beyond that point
    // so that even rehashing the table can take very long
    int newCapacity = nextPrime((int) (1 + 1.2 * size()));
    if (table.length > newCapacity) {
      rehash(newCapacity);
    }
  }

  
  @Override
  public void values(DoubleArrayList list) {
    list.setSize(distinct);
    double[] elements = list.elements();

    int j = 0;
    for (int i = state.length; i-- > 0;) {
      if (state[i] == FULL) {
        elements[j++] = values[i];
      }
    }
  }
  
  
  protected void getInternalFactors(int[] capacity, 
      double[] minLoadFactor, 
      double[] maxLoadFactor) {
    capacity[0] = table.length;
    minLoadFactor[0] = this.minLoadFactor;
    maxLoadFactor[0] = this.maxLoadFactor;
  }
}
