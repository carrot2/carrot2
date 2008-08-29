package org.carrot2.core;

import org.carrot2.util.RollingWindowAverage;

import net.sf.ehcache.Statistics;

/**
 * Provides some statistics about processing performed in a {@link CachingController}.
 */
public final class CachingControllerStatistics
{
    /**
     * Total number of queries handled, including queries resulting in an exception.
     */
    public final long totalQueries;

    /**
     * Number of queries handled without an exception.
     */
    public final long goodQueries;

    /**
     * Average clustering time measured within the {@link #algorithmTimeWindowSize}.
     */
    public final double algorithmTimeAverageInWindow;

    /**
     * Number of algorithm time measurements within the {@link #algorithmTimeWindowSize}.
     */
    public final long algorithmTimeMeasurementsInWindow;

    /**
     * Clustering average time measurement window, in milliseconds.
     */
    public final long algorithmTimeWindowSize;

    /**
     * Average document source processing time measured within the
     * {@link #sourceTimeWindowSize}.
     */
    public final double sourceTimeAverageInWindow;

    /**
     * Number of document source processing time measurements within the
     * {@link #sourceTimeWindowSize}.
     */
    public final long sourceTimeMeasurementsInWindow;

    /**
     * Document source average processing time measurement window, in milliseconds.
     */
    public final long sourceTimeWindowSize;

    /**
     * Average total processing time measured within the {@link #totalTimeWindowSize}.
     */
    public final double totalTimeAverageInWindow;

    /**
     * Number of total processing time measurements within the
     * {@link #totalTimeWindowSize}.
     */
    public final long totalTimeMeasurementsInWindow;

    /**
     * Total average processing time measurement window, in milliseconds.
     */
    public final long totalTimeWindowSize;

    /**
     * Number of requests that generated cache misses.
     */
    public final long cacheMisses;

    /**
     * Number of requests served from cache.
     */
    public final long cacheTotalHits;

    /**
     * Number of requests served from in-memory cache.
     */
    public final long cacheMemoryHits;

    /**
     * Number of requests served from on-disk cache.
     */
    public final long cacheDiskHits;

    /**
     * Populates the statistics. References to the arguments will not (and must not) be
     * retained nor exposed.
     */
    CachingControllerStatistics(CachingController.ProcessingStatistics controllerStats,
        Statistics ehcacheStats)
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
        cacheTotalHits = ehcacheStats.getCacheHits();
        cacheMemoryHits = ehcacheStats.getInMemoryHits();
        cacheDiskHits = ehcacheStats.getOnDiskHits();
    }
}
