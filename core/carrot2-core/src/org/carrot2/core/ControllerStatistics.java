
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

package org.carrot2.core;

import java.io.OutputStream;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

/**
 * Provides some statistics about processing performed in a {@link Controller}.
 */
@Root(name = "statistics")
public final class ControllerStatistics
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
     * Number of requests that generated cache misses. May be null if the controller does
     * not perform caching.
     */
    @Attribute(name = "cache-misses", required = false)
    public final Long cacheMisses;

    /**
     * Number of requests served from cache. May be null if the controller does not
     * perform caching.
     */
    @Attribute(name = "cache-hits-total", required = false)
    public final Long cacheHitsTotal;

    ControllerStatistics(long totalQueries, long goodQueries,
        double algorithmTimeAverageInWindow, long algorithmTimeMeasurementsInWindow,
        long algorithmTimeWindowSize, double sourceTimeAverageInWindow,
        long sourceTimeMeasurementsInWindow, long sourceTimeWindowSize,
        double totalTimeAverageInWindow, long totalTimeMeasurementsInWindow,
        long totalTimeWindowSize, Long cacheMisses, Long cacheHitsTotal)
    {
        this.totalQueries = totalQueries;
        this.goodQueries = goodQueries;

        this.algorithmTimeAverageInWindow = algorithmTimeAverageInWindow;
        this.algorithmTimeMeasurementsInWindow = algorithmTimeMeasurementsInWindow;
        this.algorithmTimeWindowSize = algorithmTimeWindowSize;

        this.sourceTimeAverageInWindow = sourceTimeAverageInWindow;
        this.sourceTimeMeasurementsInWindow = sourceTimeMeasurementsInWindow;
        this.sourceTimeWindowSize = sourceTimeWindowSize;

        this.totalTimeAverageInWindow = totalTimeAverageInWindow;
        this.totalTimeMeasurementsInWindow = totalTimeMeasurementsInWindow;
        this.totalTimeWindowSize = totalTimeWindowSize;

        this.cacheMisses = cacheMisses;
        this.cacheHitsTotal = cacheHitsTotal;
    }

    /**
     * Serializes this statistics object as XML stream.
     */
    public void serialize(OutputStream stream) throws Exception
    {
        new Persister().write(this, stream);
    }
}
