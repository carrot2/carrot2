package org.carrot2.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * A resource relative to a class. This resource provider caches the content of returned
 * resources and closes the stream handle in {@link #open()}.
 */
public final class ClassResource extends URLResource
{
    private Class<?> clazz;
    private String resource;

    public ClassResource(Class<?> clazz, String resource)
    {
        super(clazz.getResource(resource));

        this.clazz = clazz;
        this.resource = resource;
    }

    public InputStream open() throws IOException
    {
        return ResourceUtils.prefetch(clazz.getResourceAsStream(resource));
    }
}
