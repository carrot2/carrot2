
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

package org.carrot2.util.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.CloseableUtils;

/**
 * {@link IResourceLocator} looking for resources in a web application's execution
 * context.
 */
public final class ServletContextLocator implements IResourceLocator
{
    /**
     * Servlet context.
     */
    private final ServletContext context;

    /**
     * Webapp-relative resource.
     */
    private final class WebAppResource implements IResource
    {
        private final String resource;

        public WebAppResource(String resource)
        {
            this.resource = resource;
        }

        public InputStream open() throws IOException
        {
            return context.getResourceAsStream(resource);
        }

        @Override
        public String toString()
        {
            return "[webapp: " + resource + "]";
        }
    }

    public ServletContextLocator(ServletContext servletContext)
    {
        assert servletContext != null;
        this.context = servletContext;
    }

    @Override
    public IResource [] getAll(String resource)
    {
        // Check if the path is webapp-relative.
        if (!resource.startsWith("/"))
        {
            resource = "/" + resource;
        }

        InputStream is = context.getResourceAsStream(resource);
        CloseableUtils.close(is);

        if (is != null)
        {
            return new IResource []
            {
                new WebAppResource(resource)
            };
        }

        return new IResource [0];
    }

    @Override
    public int hashCode()
    {
        return this.context.hashCode();
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof ServletContextLocator)
        {
            return ObjectUtils.equals(this.context, ((ServletContextLocator) target).context);
        }

        return false;
    }
    
    @Override
    public String toString()
    {
        return this.getClass().getName() + " [context: " + context + "]";
    }
}
