
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.carrot2.util.StreamUtils;

import com.google.common.collect.Lists;

/**
 * Resource loading abstraction layer. Use {@link ResourceUtilsFactory} to get the default
 * {@link ResourceUtils} instance.
 */
public final class ResourceUtils
{
    /**
     * Logger instance.
     */
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * An array of resource locators (used first to last).
     */
    private final IResourceLocator [] locators;

    /**
     * No direct instantiation.
     */
    public ResourceUtils(IResourceLocator [] locators)
    {
        this.locators = Arrays.copyOf(locators, locators.length); 
    }

    /**
     * Scans all resource locators and returns matching resources.
     * 
     * @param resource Resource name.
     * @param clazz Optional class for class-relative resources.
     * @return Returns an empty array if no resource matched the given name.
     */
    public IResource [] getAll(String resource, Class<?> clazz)
    {
        final ArrayList<IResource> result = Lists.newArrayList();
        for (final IResourceLocator element : locators)
        {
            final IResource [] current = element.getAll(resource, clazz);
            // There shouldn't be too many matching resources,
            // so linear search is ok.
            for (int j = 0; j < current.length; j++)
            {
                if (!result.contains(current[j]))
                {
                    result.add(current[j]);
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            final StringBuilder buf = new StringBuilder("All matching: " + resource
                + ", ");
            for (int i = 0; i < result.size(); i++)
            {
                if (i > 0)
                {
                    buf.append(", ");
                }
                buf.append(result.get(i).toString());
            }
            if (result.size() == 0)
            {
                buf.append("(none found)");
            }
            logger.debug(buf.toString());
        }

        return result.toArray(new IResource [result.size()]);
    }

    /**
     * Scans through resource locators and returns the first matching resource.
     * 
     * @param resource Resource name.
     * @param clazz Optional class for class-relative resources.
     * @return Returns null if no resource was found for the given name.
     */
    public IResource getFirst(String resource, Class<?> clazz)
    {
        for (final IResourceLocator element : locators)
        {
            final IResource [] result = element.getAll(resource, clazz);
            if (result != null && result.length > 0)
            {
                logger.debug("First matching " + resource + ", " + result[0].toString());
                return result[0];
            }
        }
        logger.debug("First matching " + resource + ", (none found)");
        return null;
    }

    /**
     * Same as {@link #getFirst(String, Class)} but without the clazz argument.
     */
    public IResource getFirst(String resource)
    {
        return getFirst(resource, null);
    }

    /**
     * Prefetches the entire content of <code>stream</code>, closing it at the end.
     * Returns an input stream to in-memory buffer.
     */
    public static InputStream prefetch(InputStream stream) throws IOException
    {
        if (stream instanceof ByteArrayInputStream)
        {
            return stream;
        }

        final byte [] content = StreamUtils.readFullyAndClose(stream);
        return new ByteArrayInputStream(content);
    }
}
