/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** A number of utility methods for working with the {@link Executor}s framework. */
public class ExecutorServiceUtils {
  static final class AccountingThreadFactory implements ThreadFactory {
    private final ClassLoader classLoader;
    private final String baseName;
    private final AtomicInteger counter = new AtomicInteger();
    private final List<WeakReference<Thread>> threads =
        Collections.synchronizedList(new ArrayList<>());

    public AccountingThreadFactory(ClassLoader classLoader, String baseName) {
      this.classLoader = classLoader;
      this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r) {
      final Thread t =
          new Thread(r, "SharedExecutor-" + baseName + "-" + counter.getAndIncrement());
      t.setDaemon(true);
      t.setContextClassLoader(classLoader);
      threads.add(new WeakReference<>(t));
      return t;
    }

    void join() {
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

  static final class AccountingExecutorService implements ExecutorService {
    private final ExecutorService delegate;
    private final AccountingThreadFactory threadFactory;

    public AccountingExecutorService(
        int maxConcurrentThreads, AccountingThreadFactory threadFactory) {
      this.delegate = Executors.newFixedThreadPool(maxConcurrentThreads, threadFactory);
      this.threadFactory = threadFactory;
    }

    public void shutdown() {
      delegate.shutdown();
      threadFactory.join();
    }

    public List<Runnable> shutdownNow() {
      final List<Runnable> result = delegate.shutdownNow();
      threadFactory.join();
      return result;
    }

    @Override
    public boolean isShutdown() {
      return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
      return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
      return delegate.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
      return delegate.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
      return delegate.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
      return delegate.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(
        Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
      return delegate.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
      return delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
      delegate.execute(command);
    }
  }

  /**
   * @return Return an executor service with a fixed thread pool of <code>maxConcurrentThreads
   *     </code> threads and context class loader initialized to <code>clazz</code>'s context class
   *     loader.
   */
  public static ExecutorService createExecutorService(int maxConcurrentThreads, Class<?> clazz) {
    final String baseName = clazz.getSimpleName();
    final ClassLoader classLoader = clazz.getClassLoader();
    final AccountingThreadFactory threadFactory =
        new AccountingThreadFactory(classLoader, baseName);
    return new AccountingExecutorService(maxConcurrentThreads, threadFactory);
  }
}
