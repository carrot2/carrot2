package org.carrot2.webapp.util;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.carrot2.util.MapUtils;


public class RequestParameterUtils
{
    public static Map<String, Object> unpack(HttpServletRequest request)
    {
        final Map<String, Object> result = MapUtils.unpack(request.getParameterMap());
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
