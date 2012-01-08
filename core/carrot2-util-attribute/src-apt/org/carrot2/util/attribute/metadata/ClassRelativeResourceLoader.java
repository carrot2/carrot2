
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.metadata;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.processing.Messager;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

final class ClassRelativeResourceLoader extends ResourceLoader
{
    private final Class<?> clazz;

    @SuppressWarnings("unused")
    private final Messager msg;

    ClassRelativeResourceLoader(Messager msg, Class<?> clazz)
    {
        this.clazz = clazz;
        this.msg = msg;
    }

    @Override
    public void init(ExtendedProperties props)
    {
        // ignore.
    }

    /**
     * 
     */
    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException
    {
        /*
         * Do some protocol connection magic because JAR URLs are cached and this complicates
         * development (the template is not found once loaded).
         */
        URL resource = clazz.getResource(name);
        if (resource == null) 
            throw new ResourceNotFoundException("Resource not found: " + name);

        try
        {
            URLConnection connection = resource.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        }
        catch (Exception e)
        {
            throw new ResourceNotFoundException(e);
        }
    }

    /**
     * 
     */
    @Override
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    /**
     * 
     */
    @Override
    public long getLastModified(Resource resource)
    {
        return 0L;
    }
}
