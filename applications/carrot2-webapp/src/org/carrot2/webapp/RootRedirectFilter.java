
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

package org.carrot2.webapp;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * Redirect from root URI to the search servlet.
 */
public class RootRedirectFilter implements Filter
{
    public final static String PARAM_TARGET_URI = "redirect.target";

    /**
     * Target URI for the redirect.
     */
    private String targetURI;

    public void doFilter(ServletRequest req, ServletResponse resp,
        FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        final String contextPath = request.getContextPath();
        final String uri = request.getRequestURI().substring(contextPath.length());
        if ("/".equals(uri) || StringUtils.isEmpty(uri))
        {
            // According to the spec, this is a temporary redirect -- fine by us.
            response.sendRedirect(
                request.getContextPath() + response.encodeRedirectURL(targetURI));
        }
        else
        {
            chain.doFilter(req, resp);
        }
    }

    public void init(FilterConfig config) throws ServletException
    {
        this.targetURI = config.getInitParameter(PARAM_TARGET_URI);
        if (StringUtils.isEmpty(targetURI))
        {
            throw new ServletException("Missing parameter: " + PARAM_TARGET_URI);
        }
    }

    public void destroy()
    {
        // Empty
    }
}
