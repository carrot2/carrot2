
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


/**
 * Prefixes all resource names with a given prefix at lookup time and
 * delegates to another resource locator.
 */
public final class PrefixDecoratorLocator implements IResourceLocator
{
    private final IResourceLocator delegate;
    private final String prefix;

    public PrefixDecoratorLocator(IResourceLocator locator, String prefix)
    {
        this.delegate = locator;
        this.prefix = prefix;
    }

    public IResource [] getAll(String resource, Class<?> clazz)
    {
        while (resource.startsWith("/"))
        {
            resource = resource.substring(1);
        }

        return delegate.getAll(prefix + resource, clazz);
    }

}
