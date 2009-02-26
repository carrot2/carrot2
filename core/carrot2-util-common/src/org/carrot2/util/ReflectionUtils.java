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
        return Class.forName(clazzName, true, Thread.currentThread()
            .getContextClassLoader());
    }
}
