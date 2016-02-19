
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

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.velocity.VelocityInitializer;

/**
 * Processes velocity resource matching the URI path and returns it as a resource.
 */
@SuppressWarnings("serial")
public final class VelocityServlet extends HttpServlet
{
    /** Default served content. */
    private static final String DEFAULT_CONTENT_TYPE = "text/plain; charset=UTF-8";

    /**
     * Velocity instance for processing templates.
     */
    private RuntimeInstance velocity;

    /** Templates folder inside the bundle. */
    private String templatesPrefix;
    
    /**
     * Content type of the served content.
     */
    private String contentType = DEFAULT_CONTENT_TYPE;

    /*
     * 
     */
    @Override
    public void init() throws ServletException
    {
        super.init();

        if (getInitParameter("content-type") != null)
        {
            this.contentType = getInitParameter("content-type"); 
        }

        if (getInitParameter("templates-prefix") != null)
        {
            this.templatesPrefix = getInitParameter("templates-prefix"); 
        }
        else
        {
            templatesPrefix = "";
        }
        
        if (StringUtils.isEmpty(getInitParameter("bundleID")))
        {
            throw new ServletException("Init attribute required: bundleID");
        }

        velocity = VelocityInitializer.createInstance(
            getInitParameter("bundleID"), templatesPrefix);
    }

    /*
     * 
     */
    @Override
    public void destroy()
    {
        super.destroy();
        velocity = null;
    }

    /**
     * Serve the content.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        final String templatePath = req.getRequestURI();
        try
        {
            final Template t = velocity.getTemplate(templatePath);

            final VelocityContext context = new VelocityContext();
            context.put("request", req);

            HttpServletUtils.sendNoCache(resp);
            
            resp.setContentType(contentType);
            final Writer w = resp.getWriter();
            t.merge(context, w);
            w.close();
        }
        catch (ResourceNotFoundException e)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (Exception e)
        {
            Utils.logError("Template parsing failed: " + templatePath, e, false);
        }
    }
}
