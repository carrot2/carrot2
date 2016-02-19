
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

package org.carrot2.util.xsltfilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet filter that adds/ sets a HTTP header to matching URIs.
 */
public final class AddHeaderFilter implements Filter
{
    private final static String INIT_PARAM_URI_REGEXP = "uri.regexp";
    private final static String INIT_PARAM_HTTP_HEADER_PREFIX = "http:";

    private Pattern uriRegexp;
    private Map<String,String> httpHeaders = new LinkedHashMap<String,String>();

    /**
     * Place this filter into service.
     * 
     * @param filterConfig The filter configuration object
     */
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException
    {
        if (filterConfig == null)
        {
            throw new IllegalArgumentException("FilterConfig must not be null.");
        }

        for (Enumeration<String> e = filterConfig.getInitParameterNames(); e.hasMoreElements();)
        {
            String initParam = e.nextElement();
            if (initParam.startsWith(INIT_PARAM_HTTP_HEADER_PREFIX))
            {
                httpHeaders.put(
                    initParam.substring(INIT_PARAM_HTTP_HEADER_PREFIX.length()), 
                    filterConfig.getInitParameter(initParam));
            }
            else if (initParam.equals(INIT_PARAM_URI_REGEXP))
            {
                this.uriRegexp = Pattern.compile(filterConfig.getInitParameter(INIT_PARAM_URI_REGEXP));                
            }
            else
            {
                throw new ServletException("Not a valid parameter: " + initParam);
            }
        }
    }

    /**
     * Apply the XSLT stylesheet to the response and pass the result to the next filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (uriRegexp.matcher(httpRequest.getRequestURI()).matches())
        {
            for (Map.Entry<String,String> e : httpHeaders.entrySet())
            {
                httpResponse.setHeader(e.getKey(), e.getValue());
            }
        }

        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy()
    {
    }
}
