package org.carrot2.dcs;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Rewrites static content URLs based on the desired mode and forwards of web resources to
 * the default servlet.
 */
public class UrlRewriteFilter implements Filter
{
    private RequestDispatcher defaultRequestDispatcher;

    /**
     * The resources to pass through to the default servlet.
     */
    private final static Pattern STATIC = Pattern
        .compile("/.*\\.(html|js|css|png|gif|jpg|swf)");

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException
    {
        final String uri = ((HttpServletRequest) request).getServletPath();
        
        // TODO: read config option here
        final boolean searchAppMode = false;

        // In search application mode, the DCS frontent is not available
        if (searchAppMode && uri.startsWith("/dcs"))
        {
            ((HttpServletResponse) response).setStatus(404);
            return;
        }

        final String prefix = searchAppMode ? "/webapp" : "/dcs";

        // Forward root URL to the desired front-ent
        if (uri.equals("/"))
        {
            request.getRequestDispatcher(prefix + "/").forward(request, response);
            return;
        }

        // Forward / to index.html
        if (uri.endsWith("/"))
        {
            request.getRequestDispatcher(uri + "index.html").forward(request, response);
            return;
        }

        if (STATIC.matcher(uri).find())
        {
            // Static content, if already forwarded to the right front-end,
            // forward to the default servlet for serving.
            if (uri.startsWith("/dcs") || uri.startsWith("/webapp"))
            {
                defaultRequestDispatcher.forward(request, response);
            }
            else
            {
                // Forward the web resources to the actual directory. We need
                // to do this because index.html is mapped to the root url,
                // but the actual assets reside in subfolders.
                request.getRequestDispatcher(prefix + uri).forward(request, response);
            }
        }
        else
        {
            // Pass through to Jersey and other filters
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.defaultRequestDispatcher = filterConfig.getServletContext()
            .getNamedDispatcher("default");
    }
}