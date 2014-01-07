
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.File;

import org.apache.commons.lang.ObjectUtils;

/**
 * Looks up resources in a folder.
 */
public final class DirLocator implements IResourceLocator
{
    /** The folder relative to which resources are resolved. */
    private File dir;

    /**
     * Initializes the locator using the given directory. If the argument is null or a
     * non-existent folder, the locator will return an empty set of resources.
     */
    public DirLocator(File dir)
    {
        this.dir = dir;
    }

    /**
     * Initializes the locator using the given path. If the argument is null or a
     * non-existent folder, the locator will return an empty set of resources.
     */
    public DirLocator(String dirPath)
    {
        this(dirPath == null ? null : new File(dirPath));
    }

    /**
     *
     */
    @Override
    public IResource [] getAll(String resource)
    {
        if (dir != null && dir.isDirectory() && dir.canRead())
        {
            resource = resource.replace('/', File.separatorChar);
            while (resource.startsWith(File.separator))
            {
                resource = resource.substring(1);
            }

            final File resourceFile = new File(dir, resource);
            if (resourceFile.isFile() && resourceFile.canRead())
            {
                return new IResource []
                {
                    new FileResource(resourceFile)
                };
            }
        }
        return new IResource [0];
    }
    
    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(dir);
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) return true;

        if (target != null && target instanceof DirLocator)
        {
            return ObjectUtils.equals(this.dir, ((DirLocator) target).dir);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [dir: "
            + (dir == null ? "null" : dir.getAbsolutePath()) + "]";
    }
}
