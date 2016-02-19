
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
        if (locator == null)
            throw new IllegalArgumentException("Delegate locator must not be null.");
        
        if (prefix == null)
            throw new IllegalArgumentException("Prefix must not be null.");

        this.delegate = locator;
        this.prefix = prefix;
    }

    @Override
    public IResource [] getAll(String resource)
    {
        while (resource.startsWith("/"))
        {
            resource = resource.substring(1);
        }

        return delegate.getAll(prefix + resource);
    }

    @Override
    public int hashCode()
    {
        return this.prefix.hashCode() ^ delegate.hashCode();
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof PrefixDecoratorLocator)
        {
            PrefixDecoratorLocator other = (PrefixDecoratorLocator) target;
            return this.delegate.equals(other.delegate) &&
                   this.prefix.equals(other.prefix);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [prefix: "
            + prefix + ", delegate: " 
            + delegate + "]";
    }    
}
