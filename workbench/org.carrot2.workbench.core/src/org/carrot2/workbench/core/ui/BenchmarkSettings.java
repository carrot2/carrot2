package org.carrot2.workbench.core.ui;

import java.io.File;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.attribute.constraint.IsDirectory;

/**
 * Settings for {@link BenchmarkViewPage}.
 */
@Bindable
public class BenchmarkSettings
{
    /**
     * Number of benchmark rounds. Typically, around 20-30 rounds is enough to
     * get a good estimate of the average clustering time. Benchmarking
     * is performed at regular priority and any processes running in the background may
     * distort the result.
     * 
     * @group Rounds
     * @level Basic
     * @label Benchmark rounds
     */
    @Input
    @Attribute
    @IntRange(min = 1, max = 100)
    public int benchmarksRounds = 20;

    /**
     * Number of warm-up rounds. Java virtual machine's JIT (just in time) compilation
     * heavily affects execution speed. A few warm-up cycles are advised to eliminate
     * initial noisy results. 
     * 
     * @group Rounds
     * @level Basic
     * @label Warmup rounds 
     */
    @Input
    @IntRange(min = 0, max = 20)
    @Attribute
    public int warmupRounds = 5;
    
    /**
     * Directory where benchmark logs should be saved. Plain-text benchmark logs are saved
     * in the provided directory.
     * 
     * @group Logging
     * @level Medium
     * @label Log directory
     */
    @Input
    @Attribute
    @IsDirectory(mustExist = true)
    public File logDirectory;
    
    /**
     * Opens the textual benchmark log in the Workbench's editor on end (only works
     * if benchmark logging is enabled).
     * 
     * @group Logging
     * @level Medium
     * @label Open log in editor
     */
    @Input
    @Attribute
    public boolean openLogsInEditor;
}
