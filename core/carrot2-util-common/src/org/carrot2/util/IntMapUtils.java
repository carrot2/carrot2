
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.Iterator;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;

/**
 * A number of utilities for working with {@link IntIntHashMap}s.
 */
public final class IntMapUtils
{
    /**
     * Converts an {@link IntIntHashMap} into a flat int [] array. The
     * returned arrays contains [key,value] pairs and is sorted in ascending order
     * by key values.  
     */
    public static final int [] flattenSortedByKey(IntIntHashMap map)
    {
        final int [] result = new int [map.size() * 2];
        int index = 0;

        // Empirical distribution is such that most input maps have 2-3 elements. Optimize 
        // for this particular case.
        if (map.size() == 2)
        {
            final Iterator<IntIntCursor> iterator = map.iterator();
            IntIntCursor c = iterator.next();
            final int k1 = c.key;
            final int v1 = c.value;

            c = iterator.next();
            final int k2 = c.key;
            final int v2 = c.value;
            
            if (k1 <= k2)
            {
                result[0] = k1;
                result[1] = v1;
                result[2] = k2;
                result[3] = v2;
            }
            else
            {
                result[0] = k2;
                result[1] = v2;
                result[2] = k1;
                result[3] = v1;
            }
        }
        else
        {
            // For larger hash maps, use a bitset to sort keys.

            final BitSet bset = new BitSet(map.size());
            for (IntIntCursor c : map)
            {
                bset.set(c.key);
            }

            for (int key = bset.nextSetBit(0); key >= 0; key = bset.nextSetBit(key + 1))
            {
                result[index++] = key;
                result[index++] = map.get(key);
            }
        }

        return result;
    }

    /**
     * Converts an {@link IntIntHashMap} into a flat int [] array. Even indexes
     * in the flat array represent keys and the corresponding odd indexes --
     * values. Note: the order of keys in the flat array is arbitrary.
     */
    public static final int [] flatten(IntIntHashMap map)
    {
        int [] result = new int [map.size() * 2];

        int index = 0;
        for (IntIntCursor c : map)
        {
            result[index++] = c.key;
            result[index++] = c.value;
        }

        return result;
    }

    /**
     * Adds all entries from a flat [] array to the provided map. Even indexes
     * in the flat array represent keys and the corresponding odd indexes --
     * values.
     * 
     * @return the input <code>map</code> for convenience
     */
    public static final IntIntHashMap addAllFromFlattened(IntIntHashMap map, int [] flattened)
    {
        for (int i = 0; i < flattened.length / 2; i++)
        {
            final int key = flattened[i * 2];
            final int v = flattened[i * 2 + 1];
            map.putOrAdd(key, v, v);
        }
        return map;
    }

    /**
     * No instantiation.
     */
    private IntMapUtils()
    {
    }
}
