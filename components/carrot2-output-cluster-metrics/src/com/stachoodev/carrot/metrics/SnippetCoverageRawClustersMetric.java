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
 * Computes snippet coverage for the whole set of clusters, i.e. what percentage
 * of the input snippets has been included in the non-junk clusters.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetCoverageRawClustersMetric implements RawClustersMetric
{
    /**
     * Returns a {@link Double}object representing extended topic coverage for
     * the whole set of clusters.
     */
    public static final String METRIC_SNIPPET_COVERAGE = "Snippet Coverage";

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
                "Cannot calculate snippet coverage without access to original clusters");
        }

        // Covered snippets
        Set coveredDocuments = new HashSet();
        for (Iterator iter = rawClusters.iterator(); iter.hasNext();)
        {
            RawClusterBase cluster = (RawClusterBase) iter.next();
            if (cluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null)
            {
                coveredDocuments.addAll(cluster.getDocuments());
            }
        }

        // All input snippets
        int allDocuments = 0;
        for (Iterator iter = originalRawClusters.iterator(); iter.hasNext();)
        {
            RawClusterBase cluster = (RawClusterBase) iter.next();
            allDocuments += cluster.getDocuments().size();
        }

        // Get the set of cluster categories
        Map metrics = new HashMap();
        metrics.put(METRIC_SNIPPET_COVERAGE, new Double(coveredDocuments.size()
            / (double) allDocuments));

        return metrics;
    }
}