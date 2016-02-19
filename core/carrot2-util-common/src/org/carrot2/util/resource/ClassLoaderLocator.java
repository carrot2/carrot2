
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.lang.ObjectUtils;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * Scan for resources relative to a given class loader (possibly in all of this
 * loader's locations).
 */
public final class ClassLoaderLocator implements IResourceLocator
{
    private final ClassLoader loader;

    public ClassLoaderLocator(ClassLoader loader)
    {
        if (loader == null)
        {
            throw new IllegalArgumentException("Class loader must be not-null.");
        }
        
        this.loader = loader;
    }

    @Override
    public IResource [] getAll(String resource)
    {
        return getAll(loader, resource);
    }

    /**
     * 
     */
    static IResource [] getAll(ClassLoader loader, String resource)
    {
        final ArrayList<IResource> result = Lists.newArrayList();

        try
        {
            /*
             * '/'-starting resources are not found for class loaders pointing to URLs
             * on disk (Windows at least). Make them relative.
             */
            while (resource.startsWith("/"))
            {
                resource = resource.substring(1);
            }

            final Enumeration<URL> e = loader.getResources(resource);
            while (e.hasMoreElements())
            {
                URL resourceURL = e.nextElement();
                result.add(new URLResource(resourceURL));
            }
        }
        catch (IOException e)
        {
            // Fall through.
        }

        return result.toArray(new IResource [result.size()]);
    }
    
    @Override
    public int hashCode()
    {
        return this.loader.hashCode();
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof ClassLoaderLocator)
        {
            return ObjectUtils.equals(this.loader, ((ClassLoaderLocator) target).loader);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [class loader: "
            + loader + "]";
    }
}
