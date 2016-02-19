
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

package org.carrot2.util.simplexml;

import java.io.*;
import java.util.Map;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;

/**
 * Simple XML session context helpers.
 */
public final class PersisterHelpers
{
    /**
     * Resource lookup key in the serialization/ deserialization session.
     */
    private static final String RESOURCE_LOOKUP_KEY = ResourceLookup.class.getName();

    /**
     * Acquire {@link ResourceLookup} from a serialization/ deserialization session. 
     */
    public static ResourceLookup getResourceLookup(Map<Object, Object> session)
    {
        ResourceLookup resourceLookup = (ResourceLookup) session.get(RESOURCE_LOOKUP_KEY);
        if (resourceLookup == null)
        {
            throw new RuntimeException("Session does not carry resource lookup context.");
        }
        return resourceLookup;
    }

    /**
     * Create a persister with the given {@link ResourceLookup} key.
     */
    public static Persister createPersister(
        final ResourceLookup resourceLookup, Strategy strategy)
    {
        return createPersister(
            ImmutableMap.<Object, Object> of(RESOURCE_LOOKUP_KEY, resourceLookup),
            strategy);
    }

    /**
     * Read and deserialize an XML resource of class <code>clazz</code>.
     * 
     * @param <T> Class to be deserialized.
     * @param required If <code>true</code>, missing resources will throw an IOException.
     * 
     * @return Returns the deserialized resource or <code>null</code> if <code>required</code>
     * is <code>false</code>.
     */
    public static <T> T read(ResourceLookup resourceLookup, 
        String resource, Class<T> clazz, boolean required)
        throws IOException
    {
        IResource res = resourceLookup.getFirst(resource);
        if (res == null)
        {
            if (required) throw new IOException("Required resource not found: " + resource);
            return null;
        }

        InputStream inputStream = null;
        try
        {
            inputStream = new BufferedInputStream(res.open());
            try
            {
                T read = PersisterHelpers.createPersister(resourceLookup, new AnnotationStrategy()).read(clazz, inputStream);
                if (read instanceof ISourceLocationAware)
                {
                    ((ISourceLocationAware) read).setSource(res.toString());
                }
                return read;
            }
            catch (IOException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                throw new IOException(e);
            }
        }
        finally
        {
            CloseableUtils.close(inputStream);
        }
    }
    
    /**
     * Create a persister with an arbitrary session map and deserialization strategy.
     */
    private static Persister createPersister(
        Map<Object, Object> attributes, Strategy strategy)
    {
        return new Persister(new SessionInitStrategy(strategy, attributes));
    }
}
