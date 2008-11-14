
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resources;

import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Looks up resources in the thread's context class loader. 
 */
public final class ContextClassLoaderLocator implements ResourceLocator
{
    private final static Logger logger = Logger.getLogger(ContextClassLoaderLocator.class);

    /**
     * 
     */
    public Resource [] getAll(String resource, Class clazz)
    {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URL resourceURL = cl.getResource(resource);
        if (resourceURL != null) {
            return new Resource [] { new ClassLoaderResource(cl, resource) };
        }

        return new Resource [0]; 
    }
}
