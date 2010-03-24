
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;

/**
 * A number of utilities for working with {@link IntIntOpenHashMap}s.
 */
public final class IntMapUtils
{
    /**
     * Converts an {@link IntIntOpenHashMap} into a flat int [] array. Even indexes
     * in the flat array represent keys and the corresponding odd indexes --
     * values. Note: the order of keys in the flat array is arbitrary.
     */
    public static final int [] flatten(IntIntOpenHashMap map)
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
     * No instantiation.
     */
    private IntMapUtils()
    {
    }
}
