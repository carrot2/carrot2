package org.carrot2.workbench.core.ui;

import java.io.*;
import java.util.*;

import static org.apache.commons.lang.SystemUtils.*;
import org.carrot2.core.CachingController;
import org.carrot2.core.ProcessingComponentDescriptor;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Maps;

/**
 * Actual background benchmarking job.
 */
final class BenchmarkJob extends Job
{
    private final SearchInput input;
    private final BenchmarkSettings settings;

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

        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        final CachingController controller = core.getController();

        final ProcessingComponentDescriptor source = core.getComponent(input.getSourceId());
        final ProcessingComponentDescriptor algorithm = core.getComponent(input.getAlgorithmId());
        
        final Map<String, Object> attributes = 
            Maps.newHashMap(input.getAttributeValueSet().getAttributeValues());

        final int totalRounds = settings.warmupRounds + settings.benchmarksRounds;

        monitor.beginTask("Running...", totalRounds);
        for (int rounds = totalRounds; rounds > 0; rounds--)
        {
            if (monitor.isCanceled()) break;

            monitor.setTaskName("Rounds left: " + rounds);

            final long start = System.currentTimeMillis();
            controller.process(attributes, source.getId(), algorithm.getId());
            final long time = System.currentTimeMillis() - start;

            synchronized (logWriter)
            {
                logWriter.format(Locale.ENGLISH, 
                    "%4d  %8.03f\n", 
                    totalRounds - rounds, time / 1000d);
            }

            statistics = statistics.update((int) time);
            monitor.worked(1);
        }

        if (monitor.isCanceled())
        {
            logWriter.println("# (cancelled)");
        }
        else
        {
            logWriter.println("# Statistics:");
            logWriter.println("# " + statistics.toString());
        }
        
        monitor.done();
        CloseableUtils.close(logWriter);

        return Status.OK_STATUS;
    }

    /**
     * Push log headers.
     */
    private void pushLogHeaders()
    {
        if (logWriter == null) return;

        logWriter.println("# Benchmarking log.");
        logWriter.println("# Source: " + input.getSourceId());
        logWriter.println("# Algorithm: " + input.getAlgorithmId());
        logWriter.println("# Query: " + input.getAttribute(AttributeNames.QUERY));
        logWriter.println("# ");
        logWriter.println("# JVM name: " + JAVA_VM_NAME);
        logWriter.println("# JVM version: " + JAVA_VM_VERSION);
        logWriter.println("# JVM info: " + JAVA_VM_INFO);
        logWriter.println("# JVM vendor: " + JAVA_VM_VENDOR);
        logWriter.println("# OS arch: " + OS_ARCH);
        logWriter.println("# OS name: " + OS_NAME);
        logWriter.println("# OS version: " + OS_VERSION);
        logWriter.println("# ");
        logWriter.println("# Benchmark rounds: " + settings.benchmarksRounds);
        logWriter.println("# Warmup rounds: " + settings.warmupRounds);
        logWriter.println("# ");
        logWriter.println("# round_number  time[s]");
    }

    /**
     * Prepare log output.
     */
    private void prepareLogs()
    {
        final String logName = 
            String.format(Locale.ENGLISH, 
                "benchmark_%1$tF_%1$tH-%1$tM-%1$tS.log", new Date());

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