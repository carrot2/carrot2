
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

package org.carrot2.workbench.core.ui;

import static org.apache.commons.lang.SystemUtils.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.mutable.MutableInt;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Actual background benchmarking job.
 */
final class BenchmarkJob extends Job
{
    /**
     * Search input for this job.
     */
    private final SearchInput input;

    /**
     * A clone of benchmark settings for this job.
     */
    final BenchmarkSettings settings;

    /**
     * Public volatile statistics when the job is in progress.
     */
    public volatile BenchmarkStatistics statistics;

    /**
     * Shared log writer. You must synchronize on this object to write.
     */
    public PrintWriter logWriter;

    /**
     * Log file (if {@link #logWriter} is writing to a file).
     */
    public File logFile;

    public BenchmarkJob(SearchInput input, BenchmarkSettings settings)
    {
        super("Benchmarking...");

        this.settings = settings;
        this.input = input;
        this.statistics = new BenchmarkStatistics(settings.warmupRounds, settings.benchmarksRounds);
    }

    /* */
    @Override
    protected IStatus run(IProgressMonitor monitor)
    {
        prepareLogs();
        pushLogHeaders();


        // Create a pool of executing threads.
        final int totalRounds = settings.getTotalRounds();
        final CountDownLatch latch = new CountDownLatch(1);
        final MutableInt rounds = new MutableInt(totalRounds);
        final Callable<Long> benchmarkRunner = createBenchmarkRunner();
        final Thread [] pool = createBenchmarkThreads(latch, rounds, monitor, benchmarkRunner);

        // Start all benchmark threads at once and wait for all of them to finish.
        monitor.beginTask("Running...", totalRounds);
        latch.countDown();
        for (Thread t : pool)
        {
            try
            {
                t.join();
            }
            catch (InterruptedException e)
            {
                // If interrupted, fall through, but terminate all threads whenever possible.
                monitor.setCanceled(true);
            }
        }

        synchronized (logWriter)
        {
            if (monitor.isCanceled())
            {
                logWriter.println("# (cancelled)");
            }
            else
            {
                logWriter.println("# Statistics:");
                logWriter.println("# " + statistics.toString());
            }
        }

        monitor.done();
        CloseableUtils.close(logWriter);

        return Status.OK_STATUS;
    }

    /**
     * Return the benchmark runner.
     */
    private Callable<Long> createBenchmarkRunner()
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        final Controller controller = core.getController();

        final ProcessingComponentDescriptor source = core.getComponent(input.getSourceId());
        final ProcessingComponentDescriptor algorithm = core.getComponent(input.getAlgorithmId());
        
        final Map<String, Object> attributes = 
            Maps.newHashMap(input.getAttributeValueSet().getAttributeValues());
        
        return new Callable<Long>() {
            public Long call() throws Exception
            {
                final long start = System.currentTimeMillis();
                controller.process(attributes, source.getId(), algorithm.getId());
                return System.currentTimeMillis() - start;
            }
        };
    }

    /**
     * Create a set of benchmark threads.
     */
    private Thread [] createBenchmarkThreads(
        final CountDownLatch latch, 
        final MutableInt rounds, 
        final IProgressMonitor monitor,
        final Callable<Long> benchmarkRunner)
    {
        final Thread [] pool = new Thread [settings.threads];
        for (int i = 0; i < settings.threads; i++)
        {
            final int threadID = i + 1;
            pool[i] = new Thread() {
                public void run()
                {
                    while (!Thread.currentThread().isInterrupted())
                    {
                        final int totalRounds = settings.warmupRounds + settings.benchmarksRounds;
                        final int round;
                        synchronized (logWriter)
                        {
                            round = rounds.intValue();
                            if (round == 0 || monitor.isCanceled())
                            {
                                // Exit if tests finished.
                                return;
                            }
                            rounds.decrement();
                        }
    
                        long time;
                        try
                        {
                            time = benchmarkRunner.call();
                        }
                        catch (Exception e)
                        {
                            Utils.logError(e, false);
                            time = 0;
                        }
    
                        synchronized (logWriter)
                        {
                            logWriter.format(Locale.ENGLISH, 
                                "%2d  %4d  %8.03f\n", 
                                threadID, totalRounds - round, time / 1000d);

                            statistics = statistics.update((int) time);
                            monitor.worked(1);
                        }
                    }
                }
            };
            pool[i].setName("benchmark-" + i);
            pool[i].setPriority(settings.priority.threadPriority);
            pool[i].start();
        }

        return pool;
    }

    /**
     * Push log headers.
     */
    private void pushLogHeaders()
    {
        if (logWriter == null) return;

        logWriter.println("# Benchmarking log.");
        logWriter.println("# ");
        logWriter.println("# Source: " + input.getSourceId());
        logWriter.println("# Algorithm: " + input.getAlgorithmId());
        logWriter.println("# Query: " + input.getAttribute(AttributeNames.QUERY));
        logWriter.println("# Results: " + input.getAttribute(AttributeNames.RESULTS));
        logWriter.println("# ");
        logWriter.println("# JVM: " + JAVA_VM_NAME 
            + ", " + JAVA_VM_VERSION 
            + ", " + JAVA_VM_INFO
            + ", " + JAVA_VM_VENDOR);
        logWriter.println("# OS arch: " + OS_ARCH 
            + ", name: " + OS_NAME
            + ", version: " + OS_VERSION);
        logWriter.println("# ");
        logWriter.println("# Benchmark rounds: " + settings.benchmarksRounds);
        logWriter.println("# Warmup rounds: " + settings.warmupRounds);
        logWriter.println("# Threads: " + settings.threads + " (available CPUs or cores: "
            + Runtime.getRuntime().availableProcessors() + "), "
            + "running at " + settings.priority + " priority.");
        logWriter.println("# ");
        logWriter.println("# thread_id  round_number  time[s]");
    }

    /**
     * Prepare log output.
     */
    private void prepareLogs()
    {
        final String logName = 
            String.format(Locale.ENGLISH, 
                "benchmark_%1$tF_%1$tH-%1$tM-%1$tS.txt", new Date());

        try
        {
            if (this.settings.logDirectory != null)
            {
                this.logFile = new File(settings.logDirectory, logName);
            }
            else
            {
                this.logFile = new File(System.getProperty("java.io.tmpdir", "."), logName);
            }
            this.logWriter = new PrintWriter(logFile, "UTF-8");
        }
        catch (IOException e)
        {
            Utils.logError(e, false);

            this.logFile = null;
            this.logWriter = null;
        }
    }
}
