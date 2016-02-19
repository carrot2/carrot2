
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

import java.util.Locale;

/**
 * Benchmark statistics (immutable).
 */
public final class BenchmarkStatistics
{
    public final double avg;
    public final double stdDev;
    public final int min;
    public final int max;
    
    private final long sum;
    private final long sumSquares;

    public final int benchmarkRounds;
    public final int warmupRounds;
    public final int round;

    public BenchmarkStatistics(int warmupRounds, int benchmarkRounds)
    {
        this(warmupRounds, benchmarkRounds, 0, 0, 0, 0, 0, 0, 0);
    }

    private BenchmarkStatistics(int warmupRounds, int benchmarkRounds, int round,
        double avg, double stdDev, int min, int max,
        long sum, long sumSquares)
    {
        this.warmupRounds = warmupRounds;
        this.benchmarkRounds = benchmarkRounds;
        this.round = round;

        this.avg = avg;
        this.stdDev = stdDev;
        this.min = min;
        this.max = max;
        this.sum = sum;
        this.sumSquares = sumSquares;
    }

    public BenchmarkStatistics update(int timeMillis)
    {
        int count = round + 1;
        if (round >= warmupRounds)
        {
            count -= warmupRounds;
        }

        long sum = this.sum + timeMillis;
        long sumSquares = this.sumSquares + timeMillis * timeMillis;

        // Clear statistics after the warmup round.
        if (round == warmupRounds)
        {
            sum = timeMillis;
            sumSquares = timeMillis * timeMillis;
        }
        
        int min = Math.min(this.min, timeMillis);
        int max = Math.max(this.max, timeMillis);
        double avg = sum / (double) count;
        double stdev = Math.sqrt(sumSquares / (double) count - avg * avg);

        // First round of the warmup or measurement.
        if (round == warmupRounds || round == 0)
        {
            min = timeMillis;
            max = timeMillis;
        }
        
        return new BenchmarkStatistics(
            warmupRounds, benchmarkRounds, round + 1,
            avg, stdev,
            min, max, sum, sumSquares);
    }
    
    @Override
    public String toString()
    {
        return String.format(
            Locale.ENGLISH,
            "avg: %.3f sec., stddev: %.3f sec., min: %.3f sec., max: %.3f sec.\n", 
            avg / 1000d, stdDev / 1000d, min / 1000d, max / 1000d);
    }
}
