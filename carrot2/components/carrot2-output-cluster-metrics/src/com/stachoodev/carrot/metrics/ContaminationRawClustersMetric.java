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

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Computes the Contamination Measure for each cluster.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ContaminationRawClustersMetric implements RawClustersMetric
{
    /**
     * Individual cluster's contamination values as java.lang.Double objects
     * will be set in each cluster under this property name
     */
    public static final String PROPERTY_CONTAMINATION = "contamination";

    /** Contamination aggregates map key base */
    private static final String CONTAMINATION_KEY = "Cont";

    /**
     * Returns minimum contamination value in the whole set of clusters.
     */
    public static final String METRIC_MIN_CONTAMINATION = MetricsUtils.MIN_VALUE_PREFIX
        + CONTAMINATION_KEY;

    /**
     * Returns maximum contamination value in the whole set of clusters.
     */
    public static final String METRIC_MAX_CONTAMINATION = MetricsUtils.MAX_VALUE_PREFIX
        + CONTAMINATION_KEY;

    /**
     * Returns average contamination value in the whole set of clusters.
     */
    public static final String METRIC_AVG_CONTAMINATION = MetricsUtils.AVG_VALUE_PREFIX
        + CONTAMINATION_KEY;

    /**
     * Returns standard deviation of contamination value in the whole set of
     * clusters.
     */
    public static final String METRIC_STD_DEV_CONTAMINATION = MetricsUtils.STD_DEV_PREFIX
        + CONTAMINATION_KEY;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.metrics.RawClustersMetric#compute(java.util.List)
     */
    public Map compute(List rawClusters, List originalRawClusters)
    {
        if (originalRawClusters == null)
        {
            throw new IllegalArgumentException(
                "Cannot calculate contamination without access to original clusters");
        }

        // Maps catids (key) -> array indices (value)
        Map catids = new HashMap();
        int maxCatid = 0;

        // Build catid array index map
        for (Iterator clustersIter = originalRawClusters.iterator(); clustersIter
            .hasNext();)
        {
            RawCluster rawCluster = (RawCluster) clustersIter.next();
            Object catid = rawCluster
                .getProperty(RawDocumentsProducer.PROPERTY_CATID);

            if (catid == null)
            {
                throw new IllegalArgumentException(
                    "All original clusters must have the '"
                        + RawDocumentsProducer.PROPERTY_CATID + "' property");

            }
            catids.put(catid, new Integer(maxCatid++));
        }

        // Create the h matrix
        int [][] h = new int [maxCatid] [rawClusters.size()];
        int k = 0;
        for (Iterator clusterIter = rawClusters.iterator(); clusterIter
            .hasNext(); k++)
        {
            RawCluster rawCluster = (RawCluster) clusterIter.next();
            List documents = rawCluster.getDocuments();
            for (Iterator documentsIter = documents.iterator(); documentsIter
                .hasNext();)
            {
                RawDocument rawDocument = (RawDocument) documentsIter.next();
                String catid = (String) rawDocument
                    .getProperty(RawDocumentsProducer.PROPERTY_CATID);

                if (catid == null)
                {
                    throw new IllegalArgumentException(
                        "All documents must have the '"
                            + RawDocumentsProducer.PROPERTY_CATID
                            + "' property");
                }

                int c = ((Integer) catids.get(catid)).intValue();

                h[c][k]++;
            }
        }

        // Create the hRoof matrix
        int [][] hRoof = new int [maxCatid] [rawClusters.size()];
        for (k = 0; k < rawClusters.size(); k++)
        {
            int m = 0;
            for (int c = 0; c < maxCatid; c++)
            {
                m += h[c][k];
            }

            for (int c = 0; c < maxCatid; c++)
            {
                if (c < (m % maxCatid))
                {
                    hRoof[c][k] = m / maxCatid + 1;
                }
                else
                {
                    hRoof[c][k] = m / maxCatid;
                }
            }
        }

        // Calculate contamination
        for (k = 0; k < rawClusters.size(); k++)
        {
            RawClusterBase rawCluster = (RawClusterBase) rawClusters.get(k);
            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null)
            {
                continue;
            }

            double contamination = computeA(h, k);
            if (contamination != 0)
            {
                double amax = computeA(hRoof, k);
                contamination /= amax;
            }
            rawCluster.setProperty(PROPERTY_CONTAMINATION, new Double(
                contamination));
        }

        Map aggregates = new LinkedHashMap();
        MetricsUtils.addMetricStatistics(aggregates, rawClusters,
            ContaminationRawClustersMetric.PROPERTY_CONTAMINATION,
            CONTAMINATION_KEY);

        return aggregates;
    }

    /**
     * @param h
     * @param k
     * @return
     */
    private int computeA(int [][] h, int k)
    {
        int a = 0;
        for (int c = 1; c < h.length; c++)
        {
            for (int cPrimed = 0; cPrimed < c; cPrimed++)
            {
                a += h[c][k] * h[cPrimed][k];
            }
        }

        return a;
    }
}