
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

package org.carrot2.workbench.vis.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public final class HttpServletUtils
{
    private HttpServletUtils()
    {
        // No instances.
    }

    /**
     * Send no cache headers.
     */
    public static void sendNoCache(HttpServletResponse resp)
    {
        // HTTP1.1
        resp.setHeader("Cache-Control", "no-cache");

        // HTTP1.0
        resp.setHeader("Pragma", "no-cache");
    }

    /**
     * @return Returns parameter name if it exists and can be converted
     * to an integer, <code>null</code> otherwise.
     */
    public static Integer getIntParameter(HttpServletRequest request, String paramName)
    {
        final String paramValue = request.getParameter(paramName);
        if (StringUtils.isEmpty(paramValue) || !paramValue.matches("[0-9]+"))
        {
            return null;
        }

        return Integer.parseInt(paramValue);
    }
}
