package org.carrot2.util;

import com.carrotsearch.hppc.*;

/**
 * PCJ compatibility routines.
 */
public final class PcjCompat
{
    public static byte [] toByteArray(BitSet set)
    {
        if (set.length() > 0xff)
            throw new RuntimeException("BitSet conversion to index byte array failed for" 
                + " bitset length: " + set.length());
        
        final int card = (int) set.cardinality();
        final byte [] ba = new byte [card];
        for (int bi = 0, b = set.nextSetBit(0); 
            b >= 0; 
            b = set.nextSetBit(b + 1), bi++)
        {
            ba[bi] = (byte) b;
        }

        return ba;
    }

    public static int [] toIntArray(BitSet set)
    {
        final int card = (int) set.cardinality();
        final int [] ba = new int [card];
        for (int bi = 0, b = set.nextSetBit(0); 
            b >= 0; 
            b = set.nextSetBit(b + 1), bi++)
        {
            ba[bi] = b;
        }

        return ba;
    }

    public static IntIntOpenHashMap clone(IntIntOpenHashMap map)
    {
        IntIntOpenHashMap clone = new IntIntOpenHashMap(map.keys.length);

        clone.keys = new int [map.keys.length];
        System.arraycopy(map.keys, 0, clone.keys, 0, clone.keys.length);

        clone.values = new int [map.values.length];
        System.arraycopy(map.values, 0, clone.values, 0, clone.values.length);

        clone.states = new byte [map.states.length];
        System.arraycopy(map.states, 0, clone.states, 0, clone.states.length);

        clone.assigned = map.assigned;
        clone.deleted = map.deleted;

        return clone;
    }

    public static boolean equals(IntArrayList o1, IntArrayList o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        final int [] b1 = o1.buffer;
        final int [] b2 = o2.buffer;
        for (int i = o1.size() - 1; i >= 0; i--)
        {
            if (b1[i] != b2[i]) return false;
        }

        return true;
    }

    public static boolean equals(IntIntOpenHashMap o1, IntIntOpenHashMap o2)
    {
        if (o1 == o2) return true;
        if (o1.size() != o2.size()) return false;

        // In case of open-addressed hash maps, there is no other way to check
        // than just by iteration over keys.
        for (IntIntCursor c1 : o1) 
        {
            if (o2.containsKey(c1.key) && o2.lget() == c1.value)
                continue;
            return false;
        }

        return true;
    }
}
