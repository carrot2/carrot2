
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

package org.carrot2.util.resource;


/**
 * Prefixes all resource names with a given prefix at lookup time and
 * delegates to another resource locator.
 */
public final class PrefixDecoratorLocator implements ResourceLocator
{
    private final ResourceLocator delegate;
    private final String prefix;

    public PrefixDecoratorLocator(ResourceLocator locator, String prefix)
    {
        this.delegate = locator;
        this.prefix = prefix;
    }

    public Resource [] getAll(String resource, Class<?> clazz)
    {
        while (resource.startsWith("/"))
        {
            resource = resource.substring(1);
        }

        return delegate.getAll(prefix + resource, clazz);
    }

}
