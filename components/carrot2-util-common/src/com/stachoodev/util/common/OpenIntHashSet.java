/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.util.common;

import java.util.*;

import cern.colt.function.*;
import cern.colt.list.*;
import cern.colt.map.*;

/**
 * Implementation based on Colt's {@link cern.colt.map.OpenIntIntHashMap}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class OpenIntHashSet
{
    /**
     * The number of distinct associations in the map; its "size()".
     */
    protected int distinct;

    /**
     * The table capacity c=table.length always satisfies the invariant
     * <tt>c * minLoadFactor <= s <= c * maxLoadFactor</tt>, where s=size()
     * is the number of associations currently contained. The term "c *
     * minLoadFactor" is called the "lowWaterMark", "c * maxLoadFactor" is
     * called the "highWaterMark". In other words, the table capacity (and
     * proportionally the memory used by this class) oscillates within these
     * constraints. The terms are precomputed and cached to avoid recalculating
     * them each time put(..) or removeKey(...) is called.
     */
    protected int lowWaterMark;
    protected int highWaterMark;

    /**
     * The minimum load factor for the hashtable.
     */
    protected double minLoadFactor;

    /**
     * The maximum load factor for the hashtable.
     */
    protected double maxLoadFactor;

    /**
     * TODO: make defaultCapacity smaller?
     */
    protected static final int defaultCapacity = 277;
    protected static final double defaultMinLoadFactor = 0.2;
    protected static final double defaultMaxLoadFactor = 0.5;

    /**
     * The hash table keys.
     * 
     * @serial
     */
    protected int table[];

    /**
     * The state of each hash table entry (FREE, FULL, REMOVED).
     * 
     * @serial
     */
    protected byte state[];

    /**
     * The number of table entries in state==FREE.
     * 
     * @serial
     */
    protected int freeEntries;

    protected static final byte FREE = 0;
    protected static final byte FULL = 1;
    protected static final byte REMOVED = 2;

    /**
     * Constructs an empty map with default capacity and default load factors.
     */
    public OpenIntHashSet()
    {
        this(defaultCapacity);
    }

    /**
     * Constructs an empty map with the specified initial capacity and default
     * load factors.
     * 
     * @param initialCapacity the initial capacity of the map.
     * @throws IllegalArgumentException if the initial capacity is less than
     *             zero.
     */
    public OpenIntHashSet(int initialCapacity)
    {
        this(initialCapacity, defaultMinLoadFactor, defaultMaxLoadFactor);
    }

    /**
     * Constructs an empty map with the specified initial capacity and the
     * specified minimum and maximum load factor.
     * 
     * @param initialCapacity the initial capacity.
     * @param minLoadFactor the minimum load factor.
     * @param maxLoadFactor the maximum load factor.
     * @throws IllegalArgumentException if
     *             <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
     */
    public OpenIntHashSet(int initialCapacity, double minLoadFactor,
        double maxLoadFactor)
    {
        setUp(initialCapacity, minLoadFactor, maxLoadFactor);
    }

    /**
     * Removes all (key,value) associations from the receiver. Implicitly calls
     * <tt>trimToSize()</tt>.
     */
    public void clear()
    {
        Arrays.fill(state, FREE);
        //        new ByteArrayList(this.state).fillFromToWith(0, this.state.length -
        // 1,
        //            FREE);

        this.distinct = 0;
        this.freeEntries = table.length; // delta
        trimToSize();
    }

    /**
     * Returns a deep copy of the receiver.
     * 
     * @return a deep copy of the receiver.
     */
    public Object clone()
    {
        OpenIntHashSet copy;
        try
        {
            copy = (OpenIntHashSet) super.clone();
            copy.table = (int []) copy.table.clone();
            copy.state = (byte []) copy.state.clone();
            return copy;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns <tt>true</tt> if the receiver contains the specified key.
     * 
     * @return <tt>true</tt> if the receiver contains the specified key.
     */
    public boolean containsValue(int value)
    {
        return indexOfValue(value) >= 0;
    }

    /**
     * Ensures that the receiver can hold at least the specified number of
     * associations without needing to allocate new internal memory. If
     * necessary, allocates new internal memory and increases the capacity of
     * the receiver.
     * <p>
     * This method never need be called; it is for performance tuning only.
     * Calling this method before <tt>put()</tt> ing a large number of
     * associations boosts performance, because the receiver will grow only once
     * instead of potentially many times and hash collisions get less probable.
     * 
     * @param minCapacity the desired minimum capacity.
     */
    public void ensureCapacity(int minCapacity)
    {
        if (table.length < minCapacity)
        {
            int newCapacity = nextPrime(minCapacity);
            rehash(newCapacity);
        }
    }

    /**
     * Applies a procedure to each key of the receiver, if any. Note: Iterates
     * over the keys in no particular order. Subclasses can define a particular
     * order, for example, "sorted by key". All methods which <i>can </i> be
     * expressed in terms of this method (most methods can) <i>must guarantee
     * </i> to use the <i>same </i> order defined by this method, even if it is
     * no particular order. This is necessary so that, for example, methods
     * <tt>keys</tt> and <tt>values</tt> will yield association pairs, not
     * two uncorrelated lists.
     * 
     * @param procedure the procedure to be applied. Stops iteration if the
     *            procedure returns <tt>false</tt>, otherwise continues.
     * @return <tt>false</tt> if the procedure stopped before all keys where
     *         iterated over, <tt>true</tt> otherwise.
     */
    public boolean forEachValue(IntProcedure procedure)
    {
        for (int i = table.length; i-- > 0;)
        {
            if (state[i] == FULL)
                if (!procedure.apply(table[i]))
                    return false;
        }
        return true;
    }

    /**
     * @param value the key to be added to the receiver.
     * @return the index where the key would need to be inserted, if it is not
     *         already contained. Returns -index-1 if the key is already
     *         contained at slot index. Therefore, if the returned index < 0,
     *         then it is already contained at slot -index-1. If the returned
     *         index >= 0, then it is NOT already contained and should be
     *         inserted at slot index.
     */
    protected int indexOfInsertion(int value)
    {
        final int tab[] = table;
        final byte stat[] = state;
        final int length = tab.length;

        final int hash = HashFunctions.hash(value) & 0x7FFFFFFF;
        int i = hash % length;
        int decrement = hash % (length - 2);
        if (decrement == 0)
            decrement = 1;

        // stop if we find a removed or free slot, or if we find the key itself
        // do NOT skip over removed slots (yes, open addressing is like that...)
        while (stat[i] == FULL && tab[i] != value)
        {
            i -= decrement;
            //hashCollisions++;
            if (i < 0)
                i += length;
        }

        if (stat[i] == REMOVED)
        {
            // stop if we find a free slot, or if we find the key itself.
            // do skip over removed slots (yes, open addressing is like that...)
            // assertion: there is at least one FREE slot.
            int j = i;
            while (stat[i] != FREE && (stat[i] == REMOVED || tab[i] != value))
            {
                i -= decrement;
                //hashCollisions++;
                if (i < 0)
                    i += length;
            }
            if (stat[i] == FREE)
                i = j;
        }

        if (stat[i] == FULL)
        {
            // key already contained at slot i.
            // return a negative number identifying the slot.
            return -i - 1;
        }
        // not already contained, should be inserted at slot i.
        // return a number >= 0 identifying the slot.
        return i;
    }

    /**
     * @param value the key to be searched in the receiver.
     * @return the index where the key is contained in the receiver, returns -1
     *         if the key was not found.
     */
    protected int indexOfValue(int value)
    {
        final int tab[] = table;
        final byte stat[] = state;
        final int length = tab.length;

        final int hash = HashFunctions.hash(value) & 0x7FFFFFFF;
        int i = hash % length;
        int decrement = hash % (length - 2); // double hashing, see
        // http://www.eece.unm.edu/faculty/heileman/hash/node4.html
        //int decrement = (hash / length) % length;
        if (decrement == 0)
            decrement = 1;

        // stop if we find a free slot, or if we find the key itself.
        // do skip over removed slots (yes, open addressing is like that...)
        while (stat[i] != FREE && (stat[i] == REMOVED || tab[i] != value))
        {
            i -= decrement;
            //hashCollisions++;
            if (i < 0)
                i += length;
        }

        if (stat[i] == FREE)
            return -1; // not found
        return i; //found, return index where key is contained
    }

    /**
     * Fills all keys contained in the receiver into the specified list. Fills
     * the list, starting at index 0. After this call returns the specified list
     * has a new size that equals <tt>this.size()</tt>. Iteration order is
     * guaranteed to be <i>identical </i> to the order used by method
     * {@link #forEachKey(IntProcedure)}.
     * <p>
     * This method can be used to iterate over the keys of the receiver.
     * 
     * @param list the list to be filled, can have any size.
     */
    public void values(IntArrayList list)
    {
        list.setSize(distinct);
        int [] elements = list.elements();

        int [] tab = table;
        byte [] stat = state;

        int j = 0;
        for (int i = tab.length; i-- > 0;)
        {
            if (stat[i] == FULL)
                elements[j++] = tab[i];
        }
    }

    /**
     * Associates the given key with the given value. Replaces any old
     * <tt>(key,someOtherValue)</tt> association, if existing.
     * 
     * @param value the key the value shall be associated with.
     * @param value the value to be associated.
     * @return <tt>true</tt> if the receiver did not already contain such a
     *         key; <tt>false</tt> if the receiver did already contain such a
     *         key - the new value has now replaced the formerly associated
     *         value.
     */
    public boolean add(int value)
    {
        int i = indexOfInsertion(value);
        if (i < 0)
        { //already contained
            i = -i - 1;
            return false;
        }

        if (this.distinct > this.highWaterMark)
        {
            int newCapacity = chooseGrowCapacity(this.distinct + 1,
                this.minLoadFactor, this.maxLoadFactor);

            rehash(newCapacity);
            return add(value);
        }

        this.table[i] = value;
        if (this.state[i] == FREE)
            this.freeEntries--;
        this.state[i] = FULL;
        this.distinct++;

        if (this.freeEntries < 1)
        { //delta
            int newCapacity = chooseGrowCapacity(this.distinct + 1,
                this.minLoadFactor, this.maxLoadFactor);
            rehash(newCapacity);
        }

        return true;
    }

    /**
     * @param values
     */
    public void addAll(int [] values)
    {
        for (int i = 0; i < values.length; i++)
        {
            add(values[i]);
        }
    }

    /**
     * @param set
     */
    public void addAll(OpenIntHashSet set)
    {
        for (int i = set.table.length; i-- > 0;)
        {
            if (set.state[i] == FULL)
            {
                add(set.table[i]);
            }
        }
    }

    /**
     * Rehashes the contents of the receiver into a new table with a smaller or
     * larger capacity. This method is called automatically when the number of
     * keys in the receiver exceeds the high water mark or falls below the low
     * water mark.
     */
    protected void rehash(int newCapacity)
    {
        int oldCapacity = table.length;
        //if (oldCapacity == newCapacity) return;

        int oldTable[] = table;
        byte oldState[] = state;

        int newTable[] = new int [newCapacity];
        byte newState[] = new byte [newCapacity];

        this.lowWaterMark = chooseLowWaterMark(newCapacity, this.minLoadFactor);
        this.highWaterMark = chooseHighWaterMark(newCapacity,
            this.maxLoadFactor);

        this.table = newTable;
        this.state = newState;
        this.freeEntries = newCapacity - this.distinct; // delta

        for (int i = oldCapacity; i-- > 0;)
        {
            if (oldState[i] == FULL)
            {
                int element = oldTable[i];
                int index = indexOfInsertion(element);
                newTable[index] = element;
                newState[index] = FULL;
            }
        }
    }

    /**
     * Removes the given key with its associated element from the receiver, if
     * present.
     * 
     * @param key the key to be removed from the receiver.
     * @return <tt>true</tt> if the receiver contained the specified key,
     *         <tt>false</tt> otherwise.
     */
    public boolean remove(int value)
    {
        int i = indexOfValue(value);
        if (i < 0)
            return false; // key not contained

        this.state[i] = REMOVED;
        //this.values[i]=0; // delta
        this.distinct--;

        if (this.distinct < this.lowWaterMark)
        {
            int newCapacity = chooseShrinkCapacity(this.distinct,
                this.minLoadFactor, this.maxLoadFactor);
            /*
             * if (table.length != newCapacity) { System.out.print("shrink
             * rehashing "); System.out.println("at distinct="+distinct+",
             * capacity="+table.length+" to newCapacity="+newCapacity+" ..."); }
             */
            rehash(newCapacity);
        }

        return true;
    }

    /**
     * @param set
     */
    public void removeAll(OpenIntHashSet set)
    {
        for (IntIterator i = set.iterator(); i.hasNext(); )
        {
            remove(i.next());
        }
    }
    
    /**
     * Initializes the receiver.
     * 
     * @param initialCapacity the initial capacity of the receiver.
     * @param minLoadFactor the minLoadFactor of the receiver.
     * @param maxLoadFactor the maxLoadFactor of the receiver.
     * @throws IllegalArgumentException if
     *             <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
     */
    protected void setUp(int initialCapacity, double minLoadFactor,
        double maxLoadFactor)
    {
        int capacity = initialCapacity;
        capacity = nextPrime(capacity);
        if (capacity == 0)
            capacity = 1; // open addressing needs at least one FREE slot at any
        // time.

        this.table = new int [capacity];
        this.state = new byte [capacity];

        // memory will be exhausted long before this pathological case happens,
        // anyway.
        this.minLoadFactor = minLoadFactor;
        if (capacity == PrimeFinder.largestPrime)
            this.maxLoadFactor = 1.0;
        else
            this.maxLoadFactor = maxLoadFactor;

        this.distinct = 0;
        this.freeEntries = capacity; // delta

        // lowWaterMark will be established upon first expansion.
        // establishing it now (upon instance construction) would immediately
        // make the table shrink upon first put(...).
        // After all the idea of an "initialCapacity" implies violating
        // lowWaterMarks when an object is young.
        // See ensureCapacity(...)
        this.lowWaterMark = 0;
        this.highWaterMark = chooseHighWaterMark(capacity, this.maxLoadFactor);
    }

    /**
     * Trims the capacity of the receiver to be the receiver's current size.
     * Releases any superfluous internal memory. An application can use this
     * operation to minimize the storage of the receiver.
     */
    public void trimToSize()
    {
        // * 1.2 because open addressing's performance exponentially degrades
        // beyond that point
        // so that even rehashing the table can take very long
        int newCapacity = nextPrime((int) (1 + 1.2 * size()));
        if (table.length > newCapacity)
        {
            rehash(newCapacity);
        }
    }

    /**
     * Returns a prime number which is <code>&gt;= desiredCapacity</code> and
     * very close to <code>desiredCapacity</code> (within 11% if
     * <code>desiredCapacity &gt;= 1000</code>).
     * 
     * @param desiredCapacity the capacity desired by the user.
     * @return the capacity which should be used for a hashtable.
     */
    protected int nextPrime(int desiredCapacity)
    {
        return PrimeFinder.nextPrime(desiredCapacity);
    }

    /**
     * Returns the number of (key,value) associations currently contained.
     * 
     * @return the number of (key,value) associations currently contained.
     */
    public int size()
    {
        return distinct;
    }

    /**
     * Returns new high water mark threshold based on current capacity and
     * maxLoadFactor.
     * 
     * @return int the new threshold.
     */
    protected int chooseHighWaterMark(int capacity, double maxLoad)
    {
        return Math.min(capacity - 2, (int) (capacity * maxLoad));
    }

    /**
     * Chooses a new prime table capacity optimized for shrinking that
     * (approximately) satisfies the invariant
     * <tt>c * minLoadFactor <= size <= c * maxLoadFactor</tt> and has at
     * least one FREE slot for the given size.
     */
    protected int chooseShrinkCapacity(int size, double minLoad, double maxLoad)
    {
        return nextPrime(Math.max(size + 1,
            (int) ((4 * size / (minLoad + 3 * maxLoad)))));
    }

    /**
     * Returns new low water mark threshold based on current capacity and
     * minLoadFactor.
     * 
     * @return int the new threshold.
     */
    protected int chooseLowWaterMark(int capacity, double minLoad)
    {
        return (int) (capacity * minLoad);
    }

    /**
     * Chooses a new prime table capacity optimized for growing that
     * (approximately) satisfies the invariant
     * <tt>c * minLoadFactor <= size <= c * maxLoadFactor</tt> and has at
     * least one FREE slot for the given size.
     */
    protected int chooseGrowCapacity(int size, double minLoad, double maxLoad)
    {
        return nextPrime(Math.max(size + 1,
            (int) ((4 * size / (3 * minLoad + maxLoad)))));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }

        if (other == null)
        {
            return false;
        }

        if (other.getClass() != getClass())
        {
            return false;
        }

        OpenIntHashSet set = (OpenIntHashSet) other;
        if (set.size() != distinct)
        {
            return false;
        }

        for (int i = table.length; i-- > 0;)
        {
            if (state[i] == FULL)
            {
                if (!set.containsValue(table[i]))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public IntIterator iterator()
    {
        return new OpenIntHashSetIterator();
    }
    
    /**
     * 
     */
    private class OpenIntHashSetIterator implements IntIterator
    {
        int index;

        int valuesToServe;

        /**
         * @param set
         */
        public OpenIntHashSetIterator()
        {
            index = 0;
            valuesToServe = distinct;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.stachoodev.util.common.IntIterator#hasNext()
         */
        public boolean hasNext()
        {
            return valuesToServe > 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.stachoodev.util.common.IntIterator#next()
         */
        public int next()
        {
            while (index < table.length && state[index] != FULL)
            {
                index++;
            }

            if (index < table.length)
            {
                valuesToServe--;
                return table[index++];
            }
            else
            {
                return 0;
            }
        }
    }
}