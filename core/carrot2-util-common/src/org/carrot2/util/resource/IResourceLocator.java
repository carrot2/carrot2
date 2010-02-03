
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
 * An abstraction layer for resource lookup.
 *
 * @see ResourceUtils
 */
public interface IResourceLocator
{
    /**
     * Locate All matching resources and return their abstraction.
     */
    public IResource [] getAll(String resource, Class<?> clazz);
}
