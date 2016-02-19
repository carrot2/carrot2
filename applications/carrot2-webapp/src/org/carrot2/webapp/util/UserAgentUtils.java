
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

package org.carrot2.webapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Some utility class for parsing the user agent header.
 */
public class UserAgentUtils
{
    private static final Pattern MSIE_PATTERN = Pattern.compile("MSIE\\s+(\\d+)");
    
    private UserAgentUtils()
    {
    }

    /**
     * Returns true, if the user agent header is not blank and points to any browser
     * different than MSIE 6.0 or earlier.
     */
    public static boolean isModernBrowser(HttpServletRequest request)
    {
        return isModernBrowser(request.getHeader("User-Agent"));
    }

    /**
     * Returns true, if the user agent header is not blank and points to any browser
     * different than MSIE 6.0 or earlier.
     */
    public static boolean isModernBrowser(String userAgent)
    {
        if (StringUtils.isBlank(userAgent))
        {
            return false;
        }

        final int msieIndex = userAgent.indexOf("MSIE");
        if (msieIndex < 0)
        {
            return true;
        }

        final Matcher matcher = MSIE_PATTERN.matcher(userAgent);
        if (matcher.find())
        {
            final String msieVersion = matcher.group(1);
            
            if (msieVersion != null && msieVersion.length() > 0)
            {
                return Double.parseDouble(msieVersion) >= 7.0;
            }
            else
            {
                return false;
            }
        }
        else {
            // Some weird user agent, let's assume it's not modern
            return false;
        }
    }
}
