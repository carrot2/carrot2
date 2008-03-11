package org.carrot2.util.resource;

import java.io.File;

/**
 * Tries to load the resource assuming that an absolute file path was provided
 */
public final class AbsoluteFilePathLocator implements ResourceLocator
{
    /**
     * Creates a new {@link AbsoluteFilePathLocator}.
     */
    public AbsoluteFilePathLocator()
    {
    }

    /**
     * If the resource is an absolute file path, returns an array with the corresponding
     * {@link Resource}. Otherwise an empty array is returned.
     */
    public Resource [] getAll(String resource, Class<?> clazz)
    {
        final File resourceFile = new File(resource);
        if (resourceFile.isFile() && resourceFile.canRead())
        {
            return new Resource []
            {
                new FileResource(resourceFile)
            };
        }
        return new Resource [0];
    }
}
