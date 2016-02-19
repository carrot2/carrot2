
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

import java.net.URL;

import org.apache.commons.lang.ObjectUtils;

/**
 * Looks up resources relative to the given class.
 */
public class ClassLocator implements IResourceLocator
{
    /**
     * Resources will be scanned relative to this class.
     */
    private final Class<?> clazz;

    /*
     * 
     */
    public ClassLocator(Class<?> clazz)
    {
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class must be not-null.");
        }

        this.clazz = clazz;
    }

    /*
     *
     */
    @Override
    public IResource [] getAll(String resource)
    {
        URL resourceURL = clazz.getResource(resource);
        if (resourceURL != null)
        {
            return new IResource [] { new URLResource(resourceURL) };
        }

        return new IResource [0];
    }

    @Override
    public int hashCode()
    {
        return this.clazz.hashCode();
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof ClassLocator)
        {
            return ObjectUtils.equals(this.clazz, ((ClassLocator) target).clazz);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [class: "
            + clazz.getName() + "]";
    }
}
