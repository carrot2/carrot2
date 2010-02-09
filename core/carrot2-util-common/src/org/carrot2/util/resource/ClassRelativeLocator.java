
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

/**
 * Looks up resources relative to the given class and its class loader.
 */
public class ClassRelativeLocator implements IResourceLocator
{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(ClassRelativeLocator.class);

    /**
     *
     */
    public IResource [] getAll(String resource, Class<?> clazz)
    {
        if (clazz == null)
        {
            logger.warn("The class was null.");
            return new IResource [0];
        }
        
        try
        {
            final ArrayList<IResource> result = Lists.newArrayList();

            // Try the class first.
            URL resourceURL = clazz.getResource(resource);
            if (resourceURL != null)
            {
                result.add(new URLResource(resourceURL));
            }

            // Then try its class loader.
            final ClassLoader cl = clazz.getClassLoader();
            final Enumeration<URL> e = cl.getResources(resource);
            while (e.hasMoreElements())
            {
                resourceURL = e.nextElement();
                result.add(new URLResource(resourceURL));
            }
            return result.toArray(new IResource [result.size()]);
        }
        catch (final IOException e)
        {
            logger.warn("Locator failed to find resources.", e);
            return new IResource [0];
        }
    }
}
