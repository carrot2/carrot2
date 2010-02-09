
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

import java.util.concurrent.*;

/**
 * A number of utility methods for working with the {@link Executor}s framework.
 */
public class ExecutorServiceUtils
{
    /**
     * @return Return an executor service with a fixed thread pool of
     *         <code>maxConcurrentThreads</code> threads and context class loader
     *         initialized to <code>clazz</code>'s context class loader.
     *         <p>
     *         A weak reference to the returned object is saved internally to make the
     *         necessary cleanups in Web applications and other dynamic environments
     *         possible. See <a
     *         href="http://issues.carrot2.org/browse/CARROT-388">CARROT-388</a>.
     */
    public static ExecutorService createExecutorService(int maxConcurrentThreads,
        Class<?> clazz)
    {
        final ExecutorService service = Executors
            .newFixedThreadPool(maxConcurrentThreads,
                contextClassLoaderThreadFactory(clazz.getClassLoader()));

        return service;
    }

    /**
     * @return Return a new {@link ThreadFactory} that sets context class loader for newly
     *         created threads.
     */
    private static ThreadFactory contextClassLoaderThreadFactory(
        final ClassLoader clazzLoader)
    {
        final ThreadFactory tf = new ThreadFactory()
        {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();

            public Thread newThread(Runnable r)
            {
                final Thread t = delegate.newThread(r);
                t.setDaemon(true);
                t.setContextClassLoader(clazzLoader);
                return t;
            }
        };

        return tf;
    }
}
