
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.carrot.metrics;

import java.util.List;
import java.util.Map;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;

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
     * {@link RawCluster} instances.
     * 
     * @param rawClusters clusters for which the metric will be computed
     * @param originalRawClusters original partitioning of the input data
     * @return a {@link Map} of compued metrics
     */
    public Map compute(List rawClusters, List originalRawClusters);
}