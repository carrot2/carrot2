
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resources;

import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Looks up resources relative to the given class and its class loader. 
 */
public final class ClassRelativeLocator implements ResourceLocator
{
    private final static Logger logger = Logger.getLogger(ClassRelativeLocator.class);

    /**
     * 
     */
    public Resource [] getAll(String resource, Class clazz)
    {
        final ArrayList result = new ArrayList();
        
        // Try the class first.
        URL resourceURL = clazz.getResource(resource);
        if (resourceURL != null) {
            result.add(new ClassResource(clazz, resource));
        }

        // Then try its class loader.
        final ClassLoader cl = clazz.getClassLoader();
        resourceURL = cl.getResource(resource);
        if (resourceURL != null) {
            result.add(new ClassLoaderResource(cl, resource));
        }

        return (Resource []) result.toArray(new Resource[result.size()]);
    }
}
