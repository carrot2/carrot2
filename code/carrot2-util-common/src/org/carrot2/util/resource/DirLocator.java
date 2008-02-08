package org.carrot2.util.resource;

import java.io.File;


/**
 * Looks up resources in a filesystem folder. 
 */
public final class DirLocator implements ResourceLocator
{
    /** The folder relative to which resources are resolved. */
    private File dir;

    /**
     * Initializes the locator using the given path. If the
     * argument is null or a non-existent folder, the locator
     * will return an empty set of resources. 
     */
    public DirLocator(String dirPath)
    {
        if (dirPath != null) {
            final File f = new File(dirPath);
            if (f.isDirectory() && f.canRead()) {
                dir = f;
            }
        }
    }

    /**
     * 
     */
    public Resource [] getAll(String resource, Class<?> clazz)
    {
        if (dir != null) {
            resource = resource.replace('/', File.separatorChar);
            while (resource.startsWith(File.separator))
            {
                resource = resource.substring(1);
            }

            final File resourceFile = new File(dir, resource);
            if (resourceFile.isFile() && resourceFile.canRead())
            {
                return new Resource [] {
                    new FileResource(resourceFile)
                };
            }            
        }
        return new Resource [0];
    }
}
