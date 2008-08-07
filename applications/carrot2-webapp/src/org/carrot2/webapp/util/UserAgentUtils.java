package org.carrot2.webapp.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Some utility class for parsing the user agent header.
 */
public class UserAgentUtils
{
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

        final String msieVersion = userAgent.substring(msieIndex + 4,
            userAgent.indexOf(";", msieIndex + 4)).trim();
        double version = Double.parseDouble(msieVersion);

        return version >= 7.0;
    }
}
