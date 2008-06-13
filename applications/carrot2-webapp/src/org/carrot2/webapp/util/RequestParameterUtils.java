package org.carrot2.webapp.util;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;

public class RequestParameterUtils
{
    @SuppressWarnings("unchecked")
    public static Map<String, Object> unpack(HttpServletRequest request)
    {
        final Map<String, Object> result = Maps.newHashMap();

        final Map requestParametersMap = request.getParameterMap();
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

        final Cookie [] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                result.put(cookie.getName(), cookie.getValue());
            }
        }

        return result;
    }
}
