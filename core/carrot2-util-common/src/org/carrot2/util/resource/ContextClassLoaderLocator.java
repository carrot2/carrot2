
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

package org.carrot2.util.resource;

import java.net.URL;

/**
 * Looks up resources in the thread's context class loader.
 */
public final class ContextClassLoaderLocator implements IResourceLocator
{
    /**
     *
     */
    public IResource [] getAll(String resource, Class<?> clazz)
    {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URL resourceURL = cl.getResource(resource);
        if (resourceURL != null)
        {
            return new IResource []
            {
                new ClassLoaderResource(cl, resource)
            };
        }

        return new IResource [0];
    }
}
