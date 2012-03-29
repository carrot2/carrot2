
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;



/**
 * Looks up resources in the thread's context class loader.
 */
public final class ContextClassLoaderLocator implements IResourceLocator
{
    /**
     *
     */
    //@Override
    public IResource [] getAll(String resource)
    {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null)
        {
            return ClassLoaderLocator.getAll(cl, resource);
        }

        return new IResource [0];
    }


    //@Override
    public int hashCode()
    {
        return 0xbebe;
    }

    //@Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof ContextClassLoaderLocator)
        {
            return true;
        }

        return false;
    }

    //@Override
    public String toString()
    {
        return this.getClass().getName() + " [current: "
            + Thread.currentThread().getContextClassLoader() + "]";
    }
}
