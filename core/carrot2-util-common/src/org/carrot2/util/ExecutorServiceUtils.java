
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

/**
 * A number of utility methods for working with the {@link Executor}s framework.
 */
public class ExecutorServiceUtils
{
    final static class AccountingThreadFactory implements ThreadFactory
    {
        private final ClassLoader classLoader;
        private final String baseName;
        private final AtomicInteger counter = new AtomicInteger();
        private final List<WeakReference<Thread>> threads = 
            Collections.synchronizedList(Lists.<WeakReference<Thread>> newArrayList());

        public AccountingThreadFactory(ClassLoader classLoader, String baseName)
        {
            this.classLoader = classLoader;
            this.baseName = baseName;
        }

        @Override
        public Thread newThread(Runnable r)
        {
            final Thread t = new Thread(r, "SharedExecutor-" + baseName + "-" + counter.getAndIncrement());
            t.setDaemon(true);
            t.setContextClassLoader(classLoader);
            threads.add(new WeakReference<Thread>(t));
            return t;
        }

        void join()
        {
            for (WeakReference<Thread> r : threads) {
                Thread t = r.get();
                if (t != null) {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }

    final static class AccountingExecutorService implements ExecutorService {
        private ExecutorService delegate;
        private AccountingThreadFactory threadFactory;

        public AccountingExecutorService(int maxConcurrentThreads, AccountingThreadFactory threadFactory)
        {
            this.delegate = Executors.newFixedThreadPool(maxConcurrentThreads, threadFactory);
            this.threadFactory = threadFactory;
        }

        public void execute(Runnable command)
        {
            delegate.execute(command);
        }

        public void shutdown()
        {
            delegate.shutdown();
            threadFactory.join();
        }

        public List<Runnable> shutdownNow()
        {
            List<Runnable> result = delegate.shutdownNow();
            threadFactory.join();
            return result;
        }

        public boolean isShutdown()
        {
            return delegate.isShutdown();
        }

        public boolean isTerminated()
        {
            return delegate.isTerminated();
        }

        public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException
        {
            return delegate.awaitTermination(timeout, unit);
        }

        public <T> Future<T> submit(Callable<T> task)
        {
            return delegate.submit(task);
        }

        public <T> Future<T> submit(Runnable task, T result)
        {
            return delegate.submit(task, result);
        }

        public Future<?> submit(Runnable task)
        {
            return delegate.submit(task);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException
        {
            return delegate.invokeAll(tasks);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
            long timeout, TimeUnit unit) throws InterruptedException
        {
            return delegate.invokeAll(tasks, timeout, unit);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException
        {
            return delegate.invokeAny(tasks);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout,
            TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException
        {
            return delegate.invokeAny(tasks, timeout, unit);
        }
    }
    
    /**
     * @return Return an executor service with a fixed thread pool of
     *         <code>maxConcurrentThreads</code> threads and context class loader
     *         initialized to <code>clazz</code>'s context class loader.
     *         <p>
     *         A weak reference to the returned object is saved internally to make the
     *         necessary cleanups in Web applications and other dynamic environments
     *         possible. 
     *         See <a href="http://issues.carrot2.org/browse/CARROT-388">CARROT-388</a>.
     */
    public static ExecutorService createExecutorService(int maxConcurrentThreads,
        Class<?> clazz)
    {
        final String baseName = clazz.getSimpleName();
        final ClassLoader classLoader = clazz.getClassLoader();
        final AccountingThreadFactory threadFactory = 
            new AccountingThreadFactory(classLoader, baseName);
        return new AccountingExecutorService(maxConcurrentThreads, threadFactory);
    }
}
