
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.*;
import java.util.Map.Entry;


/**
 * Utilities for working with {@link Map}s.
 */
public class MapUtils
{
    private MapUtils()
    {
    }

    public static <K, V> HashMap<K, V> asHashMap(Map<K, V> map)
    {
        if (map instanceof HashMap)
        {
            return (HashMap<K, V>) map;
        }
        else
        {
            return new HashMap<K, V>(map);
        }
    }
}
