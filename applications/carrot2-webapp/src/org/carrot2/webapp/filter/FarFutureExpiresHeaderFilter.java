
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

package org.carrot2.webapp.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter that sets a far future expires header, by default 10 years ahead.
 */
public class FarFutureExpiresHeaderFilter implements Filter
{
    /** Init parameter name for the expires offset. */
    static final String EXPIRES_OFFSET_YEARS_PARAMETER_NAME = "expires-offset-years";

    /** The number of years to add to current date for the Expires header. */
    private static final int EXPIRES_OFFSET_YEARS_DEFAULT = 10;

    /** The expires offset to be used */
    private int expiresOffsetYears = EXPIRES_OFFSET_YEARS_DEFAULT;

    /** Init parameter name for the expires exclude regexp. */
    static final String USER_AGENT_EXCLUDE_REGEXP_PARAMETER_NAME = "user-agent-exclude";

    /**
     * The regexp matched against the User Agent field that can exclude some requests from
     * processing.
     */
    private static final Pattern USER_AGENT_EXCLUDE_REGEXP_DEFAULT = null;

    /** The expires offset to be used */
    private Pattern userAgentExclude = USER_AGENT_EXCLUDE_REGEXP_DEFAULT;

    public void destroy()
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException
    {
        // Act only on HTTP requests and responses
        if (request instanceof HttpServletRequest
            && response instanceof HttpServletResponse)
        {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final String userAgentString = httpRequest.getHeader("User-Agent");

            if (userAgentExclude == null || userAgentString == null
                || !userAgentExclude.matcher(userAgentString).find())
            {
                final HttpServletResponse httpResponse = (HttpServletResponse) response;

                final Calendar expiresCalendar = Calendar.getInstance();
                expiresCalendar.add(Calendar.YEAR, expiresOffsetYears);
                httpResponse.addDateHeader("Expires", expiresCalendar.getTimeInMillis());
            }
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        final String expiresOffsetString = filterConfig
            .getInitParameter(EXPIRES_OFFSET_YEARS_PARAMETER_NAME);
        if (expiresOffsetString != null)
        {
            expiresOffsetYears = Integer.parseInt(expiresOffsetString);
        }

        final String userAgentExcludeRegexpString = filterConfig
            .getInitParameter(USER_AGENT_EXCLUDE_REGEXP_PARAMETER_NAME);
        if (userAgentExcludeRegexpString != null)
        {
            userAgentExclude = Pattern.compile(userAgentExcludeRegexpString);
        }
    }
}
