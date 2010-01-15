package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.*;

/**
 * PCJ compatibility routines.
 */
public final class PcjCompat
{
    public static byte [] toByteArray(BitSet set)
    {
        final int card = (int) set.cardinality();
        final byte [] ba = new byte [card];
        for (int bi = 0, b = set.nextSetBit(0); 
            b >= 0; 
            b = set.nextSetBit(b + 1), bi++)
        {
            assert (b <= 0xff) : "BitSet conversion to index byte array failed for" 
                + " bitset size: " + card;

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

    public static void putAll(IntIntOpenHashMap into, IntIntOpenHashMap from)
    {
        for (IntIntCursor c : from)
        {
            into.put(c.key, c.value);
        }
    }
}
