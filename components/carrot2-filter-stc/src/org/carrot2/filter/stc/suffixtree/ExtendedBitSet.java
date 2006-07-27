
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.stc.suffixtree;

import java.io.IOException;
import java.util.Iterator;

/**
 * This class implements functionality of java.util.BitSet class plus some other
 * features. I had to copy-paste the code from JDK 1.3's java.util.BitSet
 * because I needed access to private fields of it. I don't know if it violates
 * any of Sun's licences - if so, the class will have to be rewritten in the
 * future. Below is the original Sun's javadoc for BitSet - please notice that
 * ALL references and functions arguments have been replaced with ExtendedBitSet
 * instead of BitSet (regardless of what the documentation in this file might
 * say).
 * 
 * This class implements a vector of bits that grows as needed. Each component
 * of the bit set has a <code>boolean</code> value. The bits of a
 * <code>BitSet</code> are indexed by nonnegative integers. Individual indexed
 * bits can be examined, set, or cleared. One <code>BitSet</code> may be used
 * to modify the contents of another <code>BitSet</code> through logical AND,
 * logical inclusive OR, and logical exclusive OR operations.
 * <p>
 * By default, all bits in the set initially have the value <code>false</code>.
 * <p>
 * Every bit set has a current size, which is the number of bits of space
 * currently in use by the bit set. Note that the size is related to the
 * implementation of a bit set, so it may change with implementation. The length
 * of a bit set relates to logical length of a bit set and is defined
 * independently of implementation.
 * 
 * @author Dawid Weiss
 * @author Arthur van Hoff
 * @author Michael McCloskey
 */
public final class ExtendedBitSet /* extends BitSet */
implements Cloneable, java.io.Serializable {

    /** Acceleration table for numberOfSetBits() method. */
    static int[] acceleratedSum;

    static {
        acceleratedSum = new int[256];

        int tb[] = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };

        for (int i = 0; i < 256; i++) {
            acceleratedSum[i] = tb[i >>> 4] + tb[i & 0x0f];
        }
    };

    /**
     * Returns the number of set bits in this ExtendedBitSet.
     * 
     * @return number of set bits in this set.
     */
    public int numberOfSetBits() {
        int result = 0;

        // copy static pointer to local variable to improve performance
        // (see Jack Shirazi: Java Performance Tuning, O'Reilly)
        int[] acceleratedSum = ExtendedBitSet.acceleratedSum;

        for (int i = unitsInUse - 1; i >= 0; i--) {
            int v = (int) (bits[i] >>> 32);

            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
            v = (int) (bits[i] & 0xffffffff);
            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
        }

        return result;
    }

    /**
     * Returns the number of set bits in a bitset which would be created after
     * XOR operation of argument and this ExtendedBitSet.
     * 
     * @return number of set bits in (this ^ set).
     */
    public int numberOfSetBitsAfterXor(ExtendedBitSet set) {
        int result = 0;
        int unitsInCommon;

        // copy static pointer to local variable to improve performance
        // (see Jack Shirazi: Java Performance Tuning, O'Reilly)
        int[] acceleratedSum = ExtendedBitSet.acceleratedSum;

        unitsInCommon = (unitsInUse > set.unitsInUse ? set.unitsInUse
                : unitsInUse);

        // perform logical XOR on bits before counting adding to result.
        int i;

        for (i = unitsInCommon - 1; i >= 0; i--) {
            int v = (int) ((bits[i] ^ set.bits[i]) >>> 32);

            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
            v = (int) ((bits[i] ^ set.bits[i]) & 0xffffffff);
            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
        }
        ;

        // Add any remaining bits
        if (unitsInCommon == set.unitsInUse) {

            // the remaining bits are from this bitset
            for (i = unitsInUse - 1; i >= unitsInCommon; i--) {
                int v = (int) (bits[i] >>> 32);

                result += acceleratedSum[(v >>> 24)];
                result += acceleratedSum[(v >>> 16) & 0xff];
                result += acceleratedSum[(v >>> 8) & 0xff];
                result += acceleratedSum[v & 0xff];
                v = (int) (bits[i] & 0xffffffff);
                result += acceleratedSum[(v >>> 24)];
                result += acceleratedSum[(v >>> 16) & 0xff];
                result += acceleratedSum[(v >>> 8) & 0xff];
                result += acceleratedSum[v & 0xff];
            }
        } else {

            // the remaining bits are from argument bitset.
            for (i = set.unitsInUse - 1; i >= unitsInCommon; i--) {
                int v = (int) (set.bits[i] >>> 32);

                result += acceleratedSum[(v >>> 24)];
                result += acceleratedSum[(v >>> 16) & 0xff];
                result += acceleratedSum[(v >>> 8) & 0xff];
                result += acceleratedSum[v & 0xff];
                v = (int) (set.bits[i] & 0xffffffff);
                result += acceleratedSum[(v >>> 24)];
                result += acceleratedSum[(v >>> 16) & 0xff];
                result += acceleratedSum[(v >>> 8) & 0xff];
                result += acceleratedSum[v & 0xff];
            }
        }

        return result;
    }

    /**
     * Returns the number of set bits in a bitset which would be created after
     * AND operation of this ExtendedBitSet and the argument passed.
     * 
     * @return number of set bits in (this & set).
     */
    public int numberOfSetBitsAfterAnd(ExtendedBitSet set) {
        int result = 0;
        int unitsInCommon;

        // copy static pointer to local variable to improve performance
        // (see Jack Shirazi: Java Performance Tuning, O'Reilly)
        int[] acceleratedSum = ExtendedBitSet.acceleratedSum;

        unitsInCommon = (unitsInUse > set.unitsInUse ? set.unitsInUse
                : unitsInUse);

        // perform logical AND on bits before adding their number to result.
        int i;

        for (i = unitsInCommon - 1; i >= 0; i--) {
            int v = (int) ((bits[i] & set.bits[i]) >>> 32);

            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
            v = (int) ((bits[i] & set.bits[i]) & 0xffffffff);
            result += acceleratedSum[(v >>> 24)];
            result += acceleratedSum[(v >>> 16) & 0xff];
            result += acceleratedSum[(v >>> 8) & 0xff];
            result += acceleratedSum[v & 0xff];
        }
        ;

        return result;
    }

    /**
     * Bitset iterator class. The current implementation is very naive -
     * iteration over all bits of this bitset. Some optimizations would be
     * useful. This iterator is not fail-safe!.
     */
    protected class BitsetIterator implements Iterator {

        /** Last set bit */
        int last;

        /** Current bit */
        int current;

        /** public constructor does all initializatons */
        public BitsetIterator() {
            last = ExtendedBitSet.this.length() - 1;
            current = -1;

            if (last != -1)
                advance();
        }

        /** hasNext */
        public boolean hasNext() {
            return last != -1 && current <= last;
        }

        /** index of the proceeding set bit */
        public Object next() {
            int value = current;

            advance();

            return new Integer(value);
        }

        /** We do not support this method */
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        /** go to the next set bit. */
        protected void advance() {
            if (current > last) {
                throw new java.util.NoSuchElementException();
            }

            for (int i = current + 1; i <= last; i++) {
                if (ExtendedBitSet.this.get(i)) {
                    current = i;

                    return;
                }
            }

            current = last + 1;
        }
    }

    /**
     * Returns the Iterator over indices of set bits in this object. The
     * iterator is not fail-safe.
     */
    public Iterator iterator() {
        return new BitsetIterator();
    }

    /*
     * BitSets are packed into arrays of "units." Currently a unit is a long,
     * which consists of 64 bits, requiring 6 address bits. The choice of unit
     * is determined purely by performance concerns.
     */
    private final static int ADDRESS_BITS_PER_UNIT = 6;

    private final static int BITS_PER_UNIT = 1 << ADDRESS_BITS_PER_UNIT;

    private final static int BIT_INDEX_MASK = BITS_PER_UNIT - 1;

    /**
     * The bits in this BitSet. The ith bit is stored in bits[i/64] at bit
     * position i % 64 (where bit position 0 refers to the least significant bit
     * and 63 refers to the most significant bit). INVARIANT: The words in
     * bits[] above unitInUse-1 are zero.
     * 
     * @serial
     */
    private long bits[]; // this should be called unit[]

    /**
     * The number of units in the logical size of this BitSet. INVARIANT:
     * unitsInUse is nonnegative. INVARIANT: bits[unitsInUse-1] is nonzero
     * unless unitsInUse is zero.
     */
    private transient int unitsInUse = 0;

    /* use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 7997698588986878753L;

    /**
     * Given a bit index return unit index containing it.
     */
    private static int unitIndex(int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_UNIT;
    }

    /**
     * Given a bit index, return a unit that masks that bit in its unit.
     */
    private static long bit(int bitIndex) {
        return 1L << (bitIndex & BIT_INDEX_MASK);
    }

    /**
     * Set the field unitsInUse with the logical size in units of the bit set.
     * WARNING:This function assumes that the number of units actually in use is
     * less than or equal to the current value of unitsInUse!
     */
    private void recalculateUnitsInUse() {

        /* Traverse the bitset until a used unit is found */
        int i;

        for (i = unitsInUse - 1; i >= 0; i--)
            if (bits[i] != 0)
                break; // this unit is in use!

        unitsInUse = i + 1; // the new logical size
    }

    /**
     * Creates a new bit set. All bits are initially <code>false</code>.
     */
    public ExtendedBitSet() {
        this(BITS_PER_UNIT);
    }

    /**
     * Creates a bit set whose initial size is large enough to explicitly
     * represent bits with indices in the range <code>0</code> through
     * <code>nbits-1</code>. All bits are initially <code>false</code>.
     * 
     * @param nbits
     *            the initial size of the bit set.
     * @exception NegativeArraySizeException
     *                if the specified initial size is negative.
     */
    public ExtendedBitSet(int nbits) {

        /* nbits can't be negative; size 0 is OK */
        if (nbits < 0)
            throw new NegativeArraySizeException(Integer.toString(nbits));

        bits = new long[(unitIndex(nbits - 1) + 1)];
    }

    /**
     * Ensures that the BitSet can hold enough units.
     * 
     * @param unitsRequired
     *            the minimum acceptable number of units.
     */
    private void ensureCapacity(int unitsRequired) {
        if (bits.length < unitsRequired) {

            /* Allocate larger of doubled size or required size */
            int request = Math.max(2 * bits.length, unitsRequired);
            long newBits[] = new long[request];

            System.arraycopy(bits, 0, newBits, 0, unitsInUse);

            bits = newBits;
        }
    }

    /**
     * Returns the "logical size" of this <code>BitSet</code>: the index of
     * the highest set bit in the <code>BitSet</code> plus one. Returns zero
     * if the <code>BitSet</code> contains no set bits.
     * 
     * @return the logical size of this <code>BitSet</code>.
     * @since 1.2
     */
    public int length() {
        if (unitsInUse == 0)
            return 0;

        int highestBit = (unitsInUse - 1) * 64;
        long highestUnit = bits[unitsInUse - 1];

        do {
            highestUnit = highestUnit >>> 1;

            highestBit++;
        } while (highestUnit > 0);

        return highestBit;
    }

    /**
     * Sets the bit specified by the index to <code>true</code>.
     * 
     * @param bitIndex
     *            a bit index.
     * @exception IndexOutOfBoundsException
     *                if the specified index is negative.
     * @since JDK1.0
     */
    public void set(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));

        int unitIndex = unitIndex(bitIndex);
        int unitsRequired = unitIndex + 1;

        if (unitsInUse < unitsRequired) {
            ensureCapacity(unitsRequired);

            bits[unitIndex] |= bit(bitIndex);
            unitsInUse = unitsRequired;
        } else {
            bits[unitIndex] |= bit(bitIndex);
        }
    }

    /**
     * Sets the bit specified by the index to <code>false</code>.
     * 
     * @param bitIndex
     *            the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException
     *                if the specified index is negative.
     * @since JDK1.0
     */
    public void clear(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));

        int unitIndex = unitIndex(bitIndex);

        if (unitIndex >= unitsInUse)
            return;

        bits[unitIndex] &= ~bit(bitIndex);

        if (bits[unitsInUse - 1] == 0)
            recalculateUnitsInUse();
    }

    /**
     * Clears all of the bits in this <code>BitSet</code> whose corresponding
     * bit is set in the specified <code>BitSet</code>.
     * 
     * @param set
     *            the <code>BitSet</code> with which to mask this
     *            <code>BitSet</code>.
     * @since 1.2
     */
    public void andNot(ExtendedBitSet set) {
        int unitsInCommon = Math.min(unitsInUse, set.unitsInUse);

        // perform logical (a & !b) on bits in common
        for (int i = 0; i < unitsInCommon; i++) {
            bits[i] &= ~set.bits[i];
        }

        recalculateUnitsInUse();
    }

    /**
     * Returns the value of the bit with the specified index. The value is
     * <code>true</code> if the bit with the index <code>bitIndex</code> is
     * currently set in this <code>BitSet</code>; otherwise, the result is
     * <code>false</code>.
     * 
     * @param bitIndex
     *            the bit index.
     * @return the value of the bit with the specified index.
     * @exception IndexOutOfBoundsException
     *                if the specified index is negative.
     */
    public boolean get(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));

        boolean result = false;
        int unitIndex = unitIndex(bitIndex);

        if (unitIndex < unitsInUse)
            result = ((bits[unitIndex] & bit(bitIndex)) != 0);

        return result;
    }

    /**
     * Performs a logical <b>AND </b> of this target bit set with the argument
     * bit set. This bit set is modified so that each bit in it has the value
     * <code>true</code> if and only if it both initially had the value
     * <code>true</code> and the corresponding bit in the bit set argument
     * also had the value <code>true</code>.
     * 
     * @param set
     *            a bit set.
     */
    public void and(ExtendedBitSet set) {
        if (this == set)
            return;

        // perform logical AND on bits in common
        int oldUnitsInUse = unitsInUse;

        unitsInUse = Math.min(unitsInUse, set.unitsInUse);

        int i;

        for (i = 0; i < unitsInUse; i++)
            bits[i] &= set.bits[i];

        // clear out units no longer used
        for (; i < oldUnitsInUse; i++)
            bits[i] = 0;

        // Recalculate units in use if necessary
        if (unitsInUse > 0 && bits[unitsInUse - 1] == 0)
            recalculateUnitsInUse();
    }

    /**
     * Performs a logical <b>OR </b> of this bit set with the bit set argument.
     * This bit set is modified so that a bit in it has the value
     * <code>true</code> if and only if it either already had the value
     * <code>true</code> or the corresponding bit in the bit set argument has
     * the value <code>true</code>.
     * 
     * @param set
     *            a bit set.
     */
    public void or(ExtendedBitSet set) {
        if (this == set)
            return;

        ensureCapacity(set.unitsInUse);

        // perform logical OR on bits in common
        int unitsInCommon = Math.min(unitsInUse, set.unitsInUse);
        int i;

        for (i = 0; i < unitsInCommon; i++)
            bits[i] |= set.bits[i];

        // copy any remaining bits
        for (; i < set.unitsInUse; i++)
            bits[i] = set.bits[i];

        if (unitsInUse < set.unitsInUse)
            unitsInUse = set.unitsInUse;
    }

    /**
     * Performs a logical <b>XOR </b> of this bit set with the bit set argument.
     * This bit set is modified so that a bit in it has the value
     * <code>true</code> if and only if one of the following statements holds:
     * <ul>
     * <li>The bit initially has the value <code>true</code>, and the
     * corresponding bit in the argument has the value <code>false</code>.
     * <li>The bit initially has the value <code>false</code>, and the
     * corresponding bit in the argument has the value <code>true</code>.
     * </ul>
     * 
     * @param set
     *            a bit set.
     */
    public void xor(ExtendedBitSet set) {
        int unitsInCommon;

        if (unitsInUse >= set.unitsInUse) {
            unitsInCommon = set.unitsInUse;
        } else {
            unitsInCommon = unitsInUse;

            int newUnitsInUse = set.unitsInUse;

            ensureCapacity(newUnitsInUse);

            unitsInUse = newUnitsInUse;
        }

        // perform logical XOR on bits in common
        int i;

        for (i = 0; i < unitsInCommon; i++)
            bits[i] ^= set.bits[i];

        // copy any remaining bits
        for (; i < set.unitsInUse; i++)
            bits[i] = set.bits[i];

        recalculateUnitsInUse();
    }

    /**
     * Returns a hash code value for this bit set. The has code depends only on
     * which bits have been set within this <code>BitSet</code>. The
     * algorithm used to compute it may be described as follows.
     * <p>
     * Suppose the bits in the <code>BitSet</code> were to be stored in an
     * array of <code>long</code> integers called, say, <code>bits</code>,
     * in such a manner that bit <code>k</code> is set in the
     * <code>BitSet</code> (for nonnegative values of <code>k</code>) if
     * and only if the expression
     * 
     * <pre>
     * ((k &gt;&gt; 6) &lt; bits.length) &amp;&amp; ((bits[k &gt;&gt; 6] &amp; (1L &lt;&lt; (bit &amp; 0x3F))) != 0)
     * </pre>
     * 
     * is true. Then the following definition of the <code>hashCode</code>
     * method would be a correct implementation of the actual algorithm:
     * 
     * <pre>
     * public synchronized int hashCode() {
     *     long h = 1234;
     *     for (int i = bits.length; --i &gt;= 0;) {
     *         h &circ;= bits[i] * (i + 1);
     *     }
     *     return (int) ((h &gt;&gt; 32) &circ; h);
     * }
     * </pre>
     * 
     * Note that the hash code values change if the set of bits is altered.
     * <p>
     * Overrides the <code>hashCode</code> method of <code>Object</code>.
     * 
     * @return a hash code value for this bit set.
     */
    public int hashCode() {
        long h = 1234;

        for (int i = bits.length; --i >= 0;)
            h ^= bits[i] * (i + 1);

        return (int) ((h >> 32) ^ h);
    }

    /**
     * Returns the number of bits of space actually in use by this
     * <code>BitSet</code> to represent bit values. The maximum element in the
     * set is the size - 1st element.
     * 
     * @return the number of bits currently in this bit set.
     */
    public int size() {
        return bits.length << ADDRESS_BITS_PER_UNIT;
    }

    /**
     * Compares this object against the specified object. The result is
     * <code>true</code> if and only if the argument is not <code>null</code>
     * and is a <code>Bitset</code> object that has exactly the same set of
     * bits set to <code>true</code> as this bit set. That is, for every
     * nonnegative <code>int</code> index <code>k</code>,
     * 
     * <pre>
     * ((BitSet) obj).get(k) == this.get(k)
     * </pre>
     * 
     * must be true. The current sizes of the two bit sets are not compared.
     * <p>
     * Overrides the <code>equals</code> method of <code>Object</code>.
     * 
     * @param obj
     *            the object to compare with.
     * @return <code>true</code> if the objects are the same;
     *         <code>false</code> otherwise.
     * @see java.util.BitSet#size()
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ExtendedBitSet))
            return false;

        if (this == obj)
            return true;

        ExtendedBitSet set = (ExtendedBitSet) obj;
        int minUnitsInUse = Math.min(unitsInUse, set.unitsInUse);

        // Check units in use by both BitSets
        for (int i = 0; i < minUnitsInUse; i++)
            if (bits[i] != set.bits[i])
                return false;

        // Check any units in use by only one BitSet (must be 0 in other)
        if (unitsInUse > minUnitsInUse) {
            for (int i = minUnitsInUse; i < unitsInUse; i++)
                if (bits[i] != 0)
                    return false;
        } else {
            for (int i = minUnitsInUse; i < set.unitsInUse; i++)
                if (set.bits[i] != 0)
                    return false;
        }

        return true;
    }

    /**
     * Cloning this <code>BitSet</code> produces a new <code>BitSet</code>
     * that is equal to it. The clone of the bit set is another bit set that has
     * exactly the same bits set to <code>true</code> as this bit set and the
     * same current size.
     * <p>
     * Overrides the <code>clone</code> method of <code>Object</code>.
     * 
     * @return a clone of this bit set.
     * @see java.util.BitSet#size()
     */
    public Object clone() {
        ExtendedBitSet result = null;

        try {
            result = (ExtendedBitSet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }

        result.bits = new long[bits.length];

        System.arraycopy(bits, 0, result.bits, 0, unitsInUse);

        return result;
    }

    /**
     * This override of readObject makes sure unitsInUse is set properly when
     * deserializing a bitset
     *  
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        // assume maximum length then find real length
        // because recalculateUnitsInUse assumes maintenance
        // or reduction in logical size
        unitsInUse = bits.length;

        recalculateUnitsInUse();
    }

    /**
     * Returns a string representation of this bit set. For every index for
     * which this <code>BitSet</code> contains a bit in the set state, the
     * decimal representation of that index is included in the result. Such
     * indeces aer listed in order from lowest to highest, separated by
     * ",$nbsp;" (a comma and a space) and surrounded by braces, resulting in
     * the usual mathematical notation for a set of integers.
     * <p>
     * Overrides the <code>toString</code> method of <code>Object</code>.
     * <p>
     * Example:
     * 
     * <pre>
     * BitSet drPepper = new BitSet();
     * </pre>
     * 
     * Now <code>drPepper.toString()</code> returns "<code>{}</code>".
     * <p>
     * 
     * <pre>
     * drPepper.set(2);
     * </pre>
     * 
     * Now <code>drPepper.toString()</code> returns "<code>{2}</code>".
     * <p>
     * 
     * <pre>
     * drPepper.set(4);
     * drPepper.set(10);
     * </pre>
     * 
     * Now <code>drPepper.toString()</code> returns "<code>{2, 4, 10}</code>".
     * 
     * @return a string representation of this bit set.
     */
    public String toString() {
        int numBits = unitsInUse << ADDRESS_BITS_PER_UNIT;
        StringBuffer buffer = new StringBuffer(8 * numBits + 2);
        String separator = "";

        buffer.append('{');

        for (int i = 0; i < numBits; i++) {
            if (get(i)) {
                buffer.append(separator);

                separator = ", ";

                buffer.append(i);
            }
        }

        buffer.append(':');
        buffer.append(this.numberOfSetBits());
        buffer.append('}');

        return buffer.toString();
    }
}

