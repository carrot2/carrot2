
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

import java.io.File;

/**
 * Tries to load the resource assuming that an absolute file path was provided
 */
public final class AbsoluteFilePathLocator implements IResourceLocator
{
    /**
     * If the resource is an absolute file path, returns an array with the corresponding
     * {@link IResource}. Otherwise an empty array is returned.
     */
    public IResource [] getAll(String resource, Class<?> clazz)
    {
        final File resourceFile = new File(resource);
        if (resourceFile.isAbsolute() && resourceFile.isFile() && resourceFile.canRead())
        {
            return new IResource []
            {
                new FileResource(resourceFile)
            };
        }
        return new IResource [0];
    }
}
