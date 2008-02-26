package org.carrot2.util.resource;

import java.net.URL;

/**
 * Looks up resources in the thread's context class loader.
 */
public final class ContextClassLoaderLocator implements ResourceLocator
{
    /**
     *
     */
    public Resource [] getAll(String resource, Class<?> clazz)
    {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URL resourceURL = cl.getResource(resource);
        if (resourceURL != null)
        {
            return new Resource []
            {
                new ClassLoaderResource(cl, resource)
            };
        }

        return new Resource [0];
    }
}
