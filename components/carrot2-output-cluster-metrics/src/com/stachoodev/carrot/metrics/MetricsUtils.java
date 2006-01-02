
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.metrics;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Utility methods for cluster metrics.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MetricsUtils
{
    public static final String STD_DEV_PREFIX = "Std Dev ";
    public static final String AVG_VALUE_PREFIX = "Avg ";
    public static final String MAX_VALUE_PREFIX = "Max ";
    public static final String MIN_VALUE_PREFIX = "Min ";

    /**
     * @param info
     * @param rawClusters
     * @param key
     */
    public static void addMetricStatistics(Map info, List rawClusters,
        String key, String name)
    {
        if (rawClusters == null && rawClusters.size() == 0)
        {
            return;
        }

        RawClusterBase [] clusters = (RawClusterBase []) rawClusters
            .toArray(new RawClusterBase [rawClusters.size()]);

        double min = 0;
        double max = 0;
        double avg = 0;
        int i = 0;
        int count = 0;
        while (i < clusters.length && clusters[i].getProperty(key) == null)
        {
            i++;
        }

        if (i == clusters.length)
        {
            return;
        }

        // Calculate min/max/avg
        min = clusters[i].getDoubleProperty(key, 0);
        max = clusters[i].getDoubleProperty(key, 0);
        for (; i < clusters.length; i++)
        {
            if (clusters[i].getProperty(key) != null)
            {
                count++;

                if (min > clusters[i].getDoubleProperty(key, 0))
                {
                    min = clusters[i].getDoubleProperty(key, 0);
                }

                if (max < clusters[i].getDoubleProperty(key, 0))
                {
                    max = clusters[i].getDoubleProperty(key, 0);
                }

                avg += clusters[i].getDoubleProperty(key, 0);
            }
        }
        avg /= count;

        // Calculate stddev
        double stdDev = 0;
        for (i = 0; i < clusters.length; i++)
        {
            if (clusters[i].getProperty(key) != null)
            {
                stdDev += (avg - clusters[i].getDoubleProperty(key, 0))
                    * (avg - clusters[i].getDoubleProperty(key, 0));
            }
        }
        stdDev = Math.sqrt(stdDev / count);

        info.put(MIN_VALUE_PREFIX + name, new Double(min));
        info.put(MAX_VALUE_PREFIX + name, new Double(max));
        info.put(AVG_VALUE_PREFIX + name, new Double(avg));
        info.put(STD_DEV_PREFIX + name, new Double(stdDev));
    }
}