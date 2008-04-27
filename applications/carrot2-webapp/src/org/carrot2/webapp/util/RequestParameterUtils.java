package org.carrot2.webapp.util;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class RequestParameterUtils
{
    @SuppressWarnings("unchecked")
    public static Map<String, Object> unwrap(Map requestParametersMap)
    {
        final Map<String, Object> result = Maps.newHashMap();
        
        for (Object entry : requestParametersMap.entrySet())
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
