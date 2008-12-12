
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import bak.pcj.map.IntKeyIntMap;
import bak.pcj.map.IntKeyIntMapIterator;

/**
 * A number of utilities for working with {@link IntKeyIntMap}s.
 */
public final class IntMapUtils
{
    /**
     * Converts an {@link IntKeyIntMap} into a flat int [] array. Even indexes
     * in the flat array represent keys and the corresponding odd indexes --
     * values. Note: the order of keys in the flat array is arbitrary.
     */
    public static final int [] flatten(IntKeyIntMap map)
    {
        int [] result = new int [map.size() * 2];

        int index = 0;
        for (IntKeyIntMapIterator it = map.entries(); it.hasNext();)
        {
            it.next();
            result[index++] = it.getKey();
            result[index++] = it.getValue();
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
