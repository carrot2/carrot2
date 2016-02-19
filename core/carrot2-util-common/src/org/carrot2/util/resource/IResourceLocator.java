
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
 * An abstraction layer for a single resource lookup source. A single source may combine
 * several actual locations (a class loader, for example).
 * 
 * @see ResourceLookup
 */
public interface IResourceLocator
{
    /**
     * Locate all matching resources and return their {@link IResource} handles.
     */
    public IResource [] getAll(String resource);
    
    /**
     * Override hash code to return consistent hash code depending on all locations
     * scanned by this locator. 
     */
    @Override
    public int hashCode();
    
    /**
     * Override equals to return <code>true</code> for locators scanning an identical
     * set of locations.
     */
    @Override
    public boolean equals(Object obj);
}
