
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
     * class loader. If class cannot be found, a {@link ClassNotFoundException} is thrown
     * and logged.
     */
    public static Class<?> classForName(String clazzName) throws ClassNotFoundException
    {
        return classForName(clazzName, true);
    }

    /**
     * Load and initialize (or return, if already defined) a given class using context
     * class loader.
     * 
     * @param clazzName class name to load
     * @param logWarning if <code>true</code>, a warning will be logged if class cannot be found
     */
    public static Class<?> classForName(String clazzName, boolean logWarning)
        throws ClassNotFoundException
    {
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {              
              return Class.forName(clazzName, true, cl);
            }
        }
        catch (SecurityException e)
        {
            if (logWarning)
            {
              org.slf4j.LoggerFactory.getLogger(ReflectionUtils.class).warn(
                  "Could not access CCL.", e);
            }
        }
        catch (ClassNotFoundException e)
        {
            // Retry with our CL.
        }

        try
        {
          return Class.forName(clazzName, true, ReflectionUtils.class.getClassLoader());
        }
        catch (Exception e)
        {
          if (logWarning)
          {
              org.slf4j.LoggerFactory.getLogger(ReflectionUtils.class).warn(
                  "Could not load class: " + clazzName + " (" + e.getMessage() + ").", e);
          }
          throw e;
        }
    }
}
