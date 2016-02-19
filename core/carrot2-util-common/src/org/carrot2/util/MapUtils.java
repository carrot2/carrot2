
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

import java.util.*;
import java.util.Map.Entry;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

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

    /**
     * Iterates through entries of the input <code>map</code> and for values being String
     * arrays puts the first element of the map in the result map. Scalar values get
     * copied to the output map unchanged.
     * <p>
     * This method might be useful for "unpacking" values from servlet HTTP requests.
     */
    @SuppressWarnings({"rawtypes"})
    public static Map<String, Object> unpack(final Map map)
    {
        final Map<String, Object> result = Maps.newHashMap();
        for (Object entry : map.entrySet())
        {
            final Map.Entry mapEntry = (Entry) entry;
            final String parameterName = (String) mapEntry.getKey();
            final String [] parameterValues = (String []) mapEntry.getValue();

            if (parameterValues.length == 1)
            {
                result.put(parameterName, parameterValues[0]);
            }
            else
            {
                result.put(parameterName, Lists.newArrayList(parameterValues));
            }
        }
        return result;
    }

    public static <K> Integer increment(Map<K, Integer> map, K key)
    {
        return increment(map, key, 1);
    }

    public static <K> Integer increment(Map<K, Integer> map, K key, int value)
    {
        final Integer current = map.get(key);
        if (current == null)
        {
            map.put(key, value);
            return value;
        }
        else
        {
            map.put(key, current + value);
            return current + value;
        }
    }
}
