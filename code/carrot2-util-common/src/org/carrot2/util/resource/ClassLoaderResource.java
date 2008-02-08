package org.carrot2.util.resource;

import java.io.*;

/**
 * A resource loaded using a class loader. This resource provider caches the content of
 * returned resources and closes the stream handle in {@link #open()}.
 */
public final class ClassLoaderResource extends URLResource
{
    private ClassLoader clazzLoader;
    private String resource;

    public ClassLoaderResource(ClassLoader cl, String resource)
    {
        super(cl.getResource(resource));

        this.clazzLoader = cl;
        this.resource = resource;
    }

    public InputStream open() throws IOException
    {
        return ResourceUtils.prefetch(clazzLoader.getResourceAsStream(resource));
    }
}
