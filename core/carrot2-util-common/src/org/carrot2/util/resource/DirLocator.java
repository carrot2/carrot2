
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

import java.io.File;
import java.util.Objects;

import org.slf4j.LoggerFactory;

/**
 * Looks up resources in a folder.
 */
public final class DirLocator implements IResourceLocator
{
    /** The folder relative to which resources are resolved. */
    private final File dir;
    private final boolean canAccess;

    /**
     * Initializes the locator using the given directory. If the argument is null or a
     * non-existent folder, the locator will return an empty set of resources.
     */
    public DirLocator(File dir)
    {
        this.dir = dir;
        
        boolean canAccess = true;
        try {
          canAccess = dir != null && 
                      dir.isDirectory() && 
                      dir.canRead() &&
                      dir.canExecute();
        } catch (SecurityException e) {
          LoggerFactory.getLogger(DirLocator.class)
            .warn("Security policy prevented access to folder: " + dir, e);          
        }

        this.canAccess = canAccess;
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
        if (canAccess)
        {
            try {
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
            } catch (SecurityException e) {
              LoggerFactory.getLogger(DirLocator.class)
                .warn("Security policy prevented access to resource: " + resource + " in folder " + dir, e);
            }
        }

        return new IResource [0];
    }

    @Override
    public int hashCode()
    {
        return canAccess ? dir.hashCode() : 0;
    }

    @Override
    public boolean equals(Object target)
    {
        if (target == this) { 
          return true;
        }

        if (target != null && target instanceof DirLocator)
        {
            DirLocator other = (DirLocator) target;
            return other.canAccess == this.canAccess &&
                   Objects.equals(other.dir, this.dir);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName() + " [dir: "
            + (canAccess ? dir.getAbsolutePath() : "<inaccessible>") + "]";
    }
}
