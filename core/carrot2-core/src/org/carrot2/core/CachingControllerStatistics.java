
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.OutputStream;

import net.sf.ehcache.Statistics;

import org.carrot2.util.RollingWindowAverage;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

/**
 * Provides some statistics about processing performed in a {@link CachingController}.
 */
@Root(name = "statistics")
public final class CachingControllerStatistics
{
    /**
     * Total number of queries handled, including queries resulting in an exception.
     */
    @Attribute(name = "total-queries")
    public final long totalQueries;

    /**
     * Number of queries handled without an exception.
     */
    @Attribute(name = "good-queries")
    public final long goodQueries;

    /**
     * Average clustering time measured within the {@link #algorithmTimeWindowSize}.
     */
    @Attribute(name = "algorithm-time-average-in-window")
    public final double algorithmTimeAverageInWindow;

    /**
     * Number of algorithm time measurements within the {@link #algorithmTimeWindowSize}.
     */
    @Attribute(name = "algorithm-time-measurements-in-window")
    public final long algorithmTimeMeasurementsInWindow;

    /**
     * Clustering average time measurement window, in milliseconds.
     */
    @Attribute(name = "algorithm-time-window-size")
    public final long algorithmTimeWindowSize;

    /**
     * Average document source processing time measured within the
     * {@link #sourceTimeWindowSize}.
     */
    @Attribute(name = "source-time-average-in-window")
    public final double sourceTimeAverageInWindow;

    /**
     * Number of document source processing time measurements within the
     * {@link #sourceTimeWindowSize}.
     */
    @Attribute(name = "source-time-measurements-in-window")
    public final long sourceTimeMeasurementsInWindow;

    /**
     * Document source average processing time measurement window, in milliseconds.
     */
    @Attribute(name = "source-time-window-size")
    public final long sourceTimeWindowSize;

    /**
     * Average total processing time measured within the {@link #totalTimeWindowSize}.
     */
    @Attribute(name = "total-time-average-in-window")
    public final double totalTimeAverageInWindow;

    /**
     * Number of total processing time measurements within the
     * {@link #totalTimeWindowSize}.
     */
    @Attribute(name = "total-time-measurements-in-window")
    public final long totalTimeMeasurementsInWindow;

    /**
     * Total average processing time measurement window, in milliseconds.
     */
    @Attribute(name = "total-time-window-size")
    public final long totalTimeWindowSize;

    /**
     * Number of requests that generated cache misses.
     */
    @Attribute(name = "cache-misses")
    public final long cacheMisses;

    /**
     * Number of requests served from cache.
     */
    @Attribute(name = "cache-hits-total")
    public final long cacheHitsTotal;

    /**
     * Number of requests served from in-memory cache.
     */
    @Attribute(name = "cache-hits-memory")
    public final long cacheHitsMemory;

    /**
     * Number of requests served from on-disk cache.
     */
    @Attribute(name = "cache-hits-disk")
    public final long cacheHitsDisk;

    /**
     * Populates the statistics. References to the arguments will not (and must not) be
     * retained nor exposed.
     */
    CachingControllerStatistics(CachingController.ProcessingStatistics controllerStats,
        Statistics ehcacheStats)
    {
        synchronized (controllerStats)
        {
            // Total queries
            totalQueries = controllerStats.totalQueries;
            goodQueries = controllerStats.goodQueries;

            // Averages
            final RollingWindowAverage algorithmTimeAverage = controllerStats.algorithmTimeAverage;
            algorithmTimeAverageInWindow = algorithmTimeAverage.getCurrentAverage();
            algorithmTimeMeasurementsInWindow = algorithmTimeAverage.getUpdatesInWindow();
            algorithmTimeWindowSize = algorithmTimeAverage.getWindowSizeMillis();

            final RollingWindowAverage sourceTimeAverage = controllerStats.sourceTimeAverage;
            sourceTimeAverageInWindow = sourceTimeAverage.getCurrentAverage();
            sourceTimeMeasurementsInWindow = sourceTimeAverage.getUpdatesInWindow();
            sourceTimeWindowSize = sourceTimeAverage.getWindowSizeMillis();

            final RollingWindowAverage totalTimeAverage = controllerStats.totalTimeAverage;
            totalTimeAverageInWindow = totalTimeAverage.getCurrentAverage();
            totalTimeMeasurementsInWindow = totalTimeAverage.getUpdatesInWindow();
            totalTimeWindowSize = totalTimeAverage.getWindowSizeMillis();

            // Cache stats
            cacheMisses = ehcacheStats.getCacheMisses();
            cacheHitsTotal = ehcacheStats.getCacheHits();
            cacheHitsMemory = ehcacheStats.getInMemoryHits();
            cacheHitsDisk = ehcacheStats.getOnDiskHits();
        }
    }

    /**
     * Serializes this statistics object as XML stream.
     */
    public void serialize(OutputStream stream) throws Exception
    {
        new Persister().write(this, stream);
    }
}
