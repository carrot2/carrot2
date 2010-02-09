
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

package org.carrot2.util;


/**
 * Utilities related to Java reflection.
 */
public final class ReflectionUtils
{
    private ReflectionUtils()
    {
        // No instances.
    }

    /**
     * Load and initialize (or return, if already defined) a given class using context
     * class loader.
     */
    public static Class<?> classForName(String clazzName) throws ClassNotFoundException
    {
        try
        {
            return Class.forName(clazzName, true, Thread.currentThread()
                .getContextClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            org.slf4j.LoggerFactory.getLogger(ReflectionUtils.class).warn("Could not load class: "
                + clazzName + " (" + e.getMessage() + ").");
            throw e;
        }
    }
}
