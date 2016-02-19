
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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.carrot2.util.xslt.TemplatesPool;

/**
 * A servlet filter applying XSLT stylesheets to the result of a request. The filter is
 * activated when the content has a MIME type equal to <code>text/xml</code> AND:
 * <ol>
 * <li>the content has a correct <code>ext-stylesheet</code> directive (custom extension) OR,</li>
 * <li>the content has a correct <code>xml-stylesheet</code> directive,</li>
 * <li>processing has not been suppressed by setting 
 * {@link XSLTFilterConstants#NO_XSLT_PROCESSING} in the request context.</li>
 * </ol>
 * Filter configuration is given through the web application descriptor file (<code>web.xml</code>).
 * 
 * <p>Example configuration using <code>ext-stylesheet</code>:
 * <pre>
 * &lt;?ext-stylesheet resource="WEB-INF/stylesheets/stylesheet.xsl" ?&gt; 
 * </pre>
 * 
 * <p>Example configuration using <code>xml-stylesheet</code> (note the URL here is servlet-container
 * relative, not application-context relative):
 * <pre>
 * &lt;?xml-stylesheet type="text/xsl" href="/stylesheets/stylesheet.xsl" ?&gt; 
 * </pre>
 */
public final class XSLTFilter implements Filter
{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(XSLTFilter.class);

    /**
     * Init parameter for the filter: if <code>true</code>, the parsed XSLT stylesheets
     * will be cached and reused. This is useful to speedup your application, but annoying
     * during development, so you can set this to <code>false</code> when you want the
     * stylesheet reloaded for each request. Default value: <code>true</code>
     */
    private final static String PARAM_TEMPLATE_CACHING = "template.caching";

    /**
     * A pool of cached stylesheets.
     */
    private TemplatesPool pool;

    /**
     * Servlet context of this filter.
     */
    private ServletContext context;

    /**
     * Place this filter into service.
     * 
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        if (filterConfig == null)
        {
            throw new IllegalArgumentException("FilterConfig must not be null.");
        }

        this.context = filterConfig.getServletContext();

        final boolean templateCaching = getBooleanInit(filterConfig, PARAM_TEMPLATE_CACHING, true);
        
        try
        {
            pool = new TemplatesPool(templateCaching);
        }
        catch (Exception e)
        {
            final String message = "Could not initialize XSLT transformers pool.";
            logger.error(message, e);
            throw new ServletException(message, e);
        }
    }

    /**
     * Returns init parameter or the default value.
     */
    private boolean getBooleanInit(FilterConfig config, String name, boolean defaultValue)
    {
        if (config.getInitParameter(name) != null)
        {
            return Boolean.valueOf(config.getInitParameter(name)).booleanValue();
        }
        else return defaultValue;
    }

    /**
     * Take this filter out of service.
     */
    public void destroy()
    {
        this.pool = null;
    }

    /**
     * Apply the XSLT stylesheet to the response and pass the result to the next filter.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate the stream and process it with the stylesheet.
        final XSLTFilterServletResponse wrappedResponse = new XSLTFilterServletResponse(
            httpResponse, httpRequest, context, pool);

        try
        {
            chain.doFilter(httpRequest, wrappedResponse);
        }
        catch (IOException t)
        {
            logger.info("I/O exception (doFilter): " + t.toString());
        }
        catch (Throwable t)
        {
            wrappedResponse.filterError("An unhandled exception occurred.", t);
        }
        finally
        {
            try
            {
                wrappedResponse.finishResponse();
            }
            catch (IOException e)
            {
                logger.info("I/O exception (finishResponse): " + e.toString());
            }
        }
    }
}
