
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.io.OutputStream;

/**
 * Provides some statistics about processing performed in a {@link Controller}.
 */
public final class ControllerStatistics
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

    ControllerStatistics(long totalQueries, long goodQueries,
        double algorithmTimeAverageInWindow, long algorithmTimeMeasurementsInWindow,
        long algorithmTimeWindowSize, double sourceTimeAverageInWindow,
        long sourceTimeMeasurementsInWindow, long sourceTimeWindowSize,
        double totalTimeAverageInWindow, long totalTimeMeasurementsInWindow,
        long totalTimeWindowSize)
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
    }
}
