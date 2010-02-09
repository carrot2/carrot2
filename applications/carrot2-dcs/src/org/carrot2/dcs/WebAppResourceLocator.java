
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;

/**
 * {@link IResourceLocator} loading resources from a web application context.
 */
final class WebAppResourceLocator implements IResourceLocator
{
    private static IResource [] EMPTY_RESULT = new IResource [0];

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
    
    public WebAppResourceLocator(ServletContext servletContext)
    {
        assert servletContext != null;
        this.context = servletContext;
    }

    public IResource [] getAll(String resource, Class<?> clazz)
    {
        // Check if the path is webapp-relative.
        if (isPathRelative(resource))
            resource = "/" + resource;

        InputStream is = context.getResourceAsStream(resource);
        CloseableUtils.close(is);

        if (is != null)
        {
            return new IResource [] { new WebAppResource(resource) };
        }

        return EMPTY_RESULT;
    }

    private boolean isPathRelative(String resource)
    {
        return !resource.startsWith("/");
    }
}
