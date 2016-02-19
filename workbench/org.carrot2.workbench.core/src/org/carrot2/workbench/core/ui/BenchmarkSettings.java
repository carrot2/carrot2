
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

import java.io.File;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.IsDirectory;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Settings for {@link BenchmarkViewPage}.
 */
@Bindable
@Root(name = "benchmark-settings")
public class BenchmarkSettings
{
    /** {@link Group} name. */
    private final static String ROUNDS = "Rounds";
    /** {@link Group} name. */
    private final static String THREADS = "Threads";
    /** {@link Group} name. */
    private final static String LOGGING = "Logging";

    /**
     * @see BenchmarkSettings#priority
     */
    public enum ThreadPriority 
    {
        HIGH(Thread.MAX_PRIORITY), 
        NORMAL(Thread.NORM_PRIORITY), 
        LOW(Thread.NORM_PRIORITY - 1), 
        IDLE(Thread.MIN_PRIORITY);

        public final int threadPriority;
        private ThreadPriority(int threadPriority)
        {
            this.threadPriority = threadPriority;
        }
    }

    /**
     * Number of benchmark rounds. Typically, around 50 rounds is enough to
     * get a good estimate of the average clustering time. Benchmarking
     * is performed at regular priority and any processes running in the background may
     * distort the result.
     */
    @Input
    @Attribute
    @IntRange(min = 1, max = 1000)
    @Element
    @Label("Benchmark rounds")
    @Level(AttributeLevel.BASIC)
    @Group(ROUNDS)
    public int benchmarksRounds = 75;

    /**
     * Number of warm-up rounds. Java virtual machine's JIT (just in time) compilation
     * heavily affects execution speed. A few warm-up cycles are advised to eliminate
     * initial noisy results. 
     */
    @Input
    @IntRange(min = 0, max = 200)
    @Attribute
    @Element
    @Label("Warmup rounds")
    @Level(AttributeLevel.BASIC)
    @Group(ROUNDS)
    public int warmupRounds = 25;

    /**
     * Execution priority for benchmark threads. Setting high thread priority may
     * block the user interface, but will give more accurate result.
     * Low thread priority may cause distortions due to other system processes
     * running in the background.  
     */
    @Required
    @Input
    @Attribute
    @Element
    @Label("Thread priority")
    @Level(AttributeLevel.MEDIUM)
    @Group(THREADS)
    public ThreadPriority priority = ThreadPriority.NORMAL;

    /**
     * The number of concurrently executing benchmark threads. For multi-core
     * processor, this setting can be used to saturate the CPU. 
     */
    @Required
    @Input
    @Attribute
    @IntRange(min = 1, max = 8)
    @Element
    @Label("Number of concurrent threads")
    @Level(AttributeLevel.MEDIUM)
    @Group(THREADS)
    public int threads = 1;

    // TODO: Add execution controller type (Cached, Simple)

    /**
     * Directory where benchmark logs should be saved. Plain-text benchmark logs are saved
     * in the provided directory.
     */
    @Input
    @Attribute
    @IsDirectory(mustExist = true)
    @Element(required = false)
    @Label("Log directory")
    @Level(AttributeLevel.MEDIUM)
    @Group(LOGGING)
    public File logDirectory;
    
    /**
     * Opens the textual benchmark log in the Workbench's editor on end (only works
     * if benchmark logging is enabled).
     */
    @Input
    @Attribute
    @Element
    @Label("Open log in editor")
    @Level(AttributeLevel.MEDIUM)
    @Group(LOGGING)
    public boolean openLogsInEditor;

    /**
     * A shortcut for:
     * <pre>
     * benchmarksRounds + warmupRounds
     * </pre>
     */
    public int getTotalRounds() 
    {
        return this.benchmarksRounds + this.warmupRounds;
    }
}
