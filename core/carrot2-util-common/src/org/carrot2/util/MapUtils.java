package org.carrot2.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

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
    @SuppressWarnings("unchecked")
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
                result.put(parameterName, parameterValues);
            }
        }
        return result;
    }
}
