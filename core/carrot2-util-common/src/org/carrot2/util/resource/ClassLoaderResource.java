
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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.StreamUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A resource loaded using a class loader. This loader provides cached content of
 * returned resources and closes the stream handle in {@link #open()}.
 */
@Root(name = "class-loader-resource")
public final class ClassLoaderResource implements IResource
{
    private ClassLoader clazzLoader;

    @Attribute
    private String resource;

    /**
     * For XML serialization/deserialization only, use
     * {@link #ClassLoaderResource(ClassLoader, String)}.
     */
    ClassLoaderResource()
    {
        this(null);
    }

    public ClassLoaderResource(String resource)
    {
        this(Thread.currentThread().getContextClassLoader(), resource);
    }

    public ClassLoaderResource(ClassLoader cl, String resource)
    {
        this.clazzLoader = cl;
        this.resource = resource;
    }

    public InputStream open() throws IOException
    {
        InputStream resourceAsStream = clazzLoader.getResourceAsStream(resource);
        if (resourceAsStream == null)
        {
            throw new IOException("Resource not found: " + toString());
        }
        return StreamUtils.prefetch(resourceAsStream);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ClassLoaderResource)
        {
            return ObjectUtils.equals(((ClassLoaderResource) obj).resource, resource)
                && ObjectUtils.equals(((ClassLoaderResource) obj).clazzLoader, clazzLoader);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(clazzLoader) ^ ObjectUtils.hashCode(resource);
    }

    @Override
    public String toString()
    {
        return "[CLASSPATH RESOURCE: " + resource + "@" + clazzLoader.toString() + "]";
    }
    
    /**
     * Restores a {@link ClassLoaderResource} from a string, resolving against the current context
     * class loader.
     */
    public static ClassLoaderResource valueOf(String name)
    {
        // Return non-null value only if the name is an existing resource (resolved
        // relative to context class loader).
        try {
            ClassLoaderResource res = new ClassLoaderResource(name);
            res.open().close();
            return res;
        } catch (IOException e) {
            return null;
        }
    }
}
