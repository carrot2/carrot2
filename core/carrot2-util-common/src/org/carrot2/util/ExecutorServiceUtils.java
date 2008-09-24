package org.carrot2.util;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;

import com.google.common.collect.Lists;

/**
 * A number of utility methods for working with the {@link Executor}s framework.
 */
public class ExecutorServiceUtils
{
    /**
     * A list of weak references to all {@link ExecutorService}s returned from
     * {@link #createExecutorService(int, Class)}.
     */
    private static ArrayList<WeakReference<ExecutorService>> services = Lists
        .newArrayList();

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

        synchronized (ExecutorServiceUtils.class)
        {
            /*
             * Cleanup dead references.
             */
            for (Iterator<WeakReference<ExecutorService>> i = services.iterator(); i
                .hasNext();)
            {
                if (i.next().get() == null) i.remove();
            }

            services.add(new WeakReference<ExecutorService>(service));
        }

        return service;
    }

    /**
     * This method is only for internal use.
     * 
     * @see http://issues.carrot2.org/browse/CARROT-388
     */
    public static Collection<ExecutorService> getAllCreated()
    {
        synchronized (ExecutorServiceUtils.class)
        {
            final ArrayList<ExecutorService> result = Lists
                .newArrayListWithExpectedSize(services.size());

            for (WeakReference<ExecutorService> ref : services)
            {
                final ExecutorService service = ref.get();
                if (service != null) result.add(service);
            }

            return result;
        }
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
