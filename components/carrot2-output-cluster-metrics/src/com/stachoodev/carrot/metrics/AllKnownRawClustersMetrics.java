/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.metrics;

/**
 * Provides access to all known implementation of the
 * {@link com.stachoodev.carrot.metrics.RawClustersMetric}interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class AllKnownRawClustersMetrics
{
    /** An array of all available clusters metrics */
    public static RawClustersMetric [] metrics = new RawClustersMetric []
    { new ContaminationRawClustersMetric(), new CoverageRawClustersMetric() };

    /**
     * Returns an array of all known implementation of the
     * {@link com.stachoodev.carrot.metrics.RawClustersMetric}interface.
     * 
     * @return an array of all known implementation of the
     *         {@link com.stachoodev.carrot.metrics.RawClustersMetric}
     *         interface.
     */
    public static RawClustersMetric [] getAllRawClustersMetrics()
    {
        return metrics;
    }
}