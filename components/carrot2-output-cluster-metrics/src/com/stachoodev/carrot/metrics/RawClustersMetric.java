/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.metrics;

import java.util.*;

/**
 * Defines the interface of an algorithm computing some cluster metric.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface RawClustersMetric
{
    /**
     * Computes some cluster metric for given <code>rawClusters</code> list.
     * Original partitioning is also available in the form of a list of
     * {@link RawCluster}instances.
     * 
     * @param rawClusters clusters for which the metric will be computed
     * @param originalRawClusters original partitioning of the input data
     * @return a {@link Map} of compued metrics
     */
    public Map compute(List rawClusters, List originalRawClusters);
}