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

import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Computes cluster coverage for the whole set of clusters.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TopicCoverageRawClustersMetric implements RawClustersMetric
{
    /**
     * Returns a {@link String}object representing the predominant document
     * catid in a RawCluster. In case of equal number of documents from
     * different categories, null is returned.
     */
    public static final String PROPERTY_CLUSTER_PREDOMINANT_CATID = "cluscatid";

    /**
     * Returns a {@link Double}object representing extended topic coverage for
     * the whole set of clusters.
     */
    public static final String METRIC_EXTENDED_TOPIC_COVERAGE = "Extended Topic Coverage";

    /**
     * Returns a {@link Double}object representing simple topic coverage for
     * the whole set of clusters.
     */
    public static final String METRIC_SIMPLE_TOPIC_COVERAGE = "Simple Topic Coverage";

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
                "Cannot calculate topic coverage without access to original clusters");
        }

        // Coverage metric undefined when no clusters
        if (rawClusters.size() == 0)
        {
            return new HashMap();
        }

        // For each cluster, compute the predominant catid
        computePredominantCatid(rawClusters);

        // Get the set of cluster categories
        Set clusterCatids = new HashSet();
        int assessedClusters = 0;
        for (Iterator clustersIter = rawClusters.iterator(); clustersIter
            .hasNext();)
        {
            RawCluster rawCluster = (RawCluster) clustersIter.next();

            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) == null)
            {
                assessedClusters++;
            }

            Object clusterCatid = rawCluster
                .getProperty(PROPERTY_CLUSTER_PREDOMINANT_CATID);
            if (clusterCatid != null && !clusterCatids.contains(clusterCatid))
            {
                clusterCatids.add(clusterCatid);
            }
        }

        // Get the set of all categories
        Set allCatids = new HashSet();
        for (Iterator iter = originalRawClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            Object catid = rawCluster
                .getProperty(RawDocumentsProducer.PROPERTY_CATID);
            if (catid == null)
            {
                throw new IllegalArgumentException(
                    "All original clusters must have the '"
                        + RawDocumentsProducer.PROPERTY_CATID + "' property");

            }
            allCatids.add(catid);
        }

        // Calculate coverage cutoff
        int allCategories = allCatids.size();
        int coveredCategories = clusterCatids.size();
        int fullCoveragePosition = 0;

        // Coverage metric undefined when clusters have no content
        if (clusterCatids.size() == 0)
        {
            return Collections.EMPTY_MAP;
        }

        if (coveredCategories < allCategories)
        {
            fullCoveragePosition = assessedClusters;
        }
        else
        {
            for (Iterator clustersIter = rawClusters.iterator(); clustersIter
                .hasNext();)
            {
                RawCluster rawCluster = (RawCluster) clustersIter.next();

                if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null)
                {
                    continue;
                }

                if (clusterCatids.size() == 0)
                {
                    break;
                }
                else
                {
                    clusterCatids.remove(rawCluster
                        .getProperty(PROPERTY_CLUSTER_PREDOMINANT_CATID));
                    fullCoveragePosition++;
                }
            }
        }

        Map metrics = new HashMap();
        metrics.put(METRIC_EXTENDED_TOPIC_COVERAGE, new Double(
            (coveredCategories / Math
                .sqrt(fullCoveragePosition * allCategories))));
        metrics.put(METRIC_SIMPLE_TOPIC_COVERAGE, new Double(coveredCategories
            / (double)allCategories));

        return metrics;
    }

    /**
     * @param rawClusters
     */
    private void computePredominantCatid(List rawClusters)
    {
        // Determine catids corresponding to clusters
        Map catidFreq = new HashMap();
        for (Iterator clustersIter = rawClusters.iterator(); clustersIter
            .hasNext();)
        {
            RawCluster rawCluster = (RawCluster) clustersIter.next();

            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null)
            {
                continue;
            }

            List documents = rawCluster.getDocuments();
            catidFreq.clear();

            // Determine the number of documents with each catid in the cluster
            for (Iterator documentsIter = documents.iterator(); documentsIter
                .hasNext();)
            {
                RawDocument document = (RawDocument) documentsIter.next();
                String catid = (String) document
                    .getProperty(RawDocumentsProducer.PROPERTY_CATID);
                if (catid == null)
                {
                    throw new IllegalArgumentException(
                        "All documents must have the '"
                            + RawDocumentsProducer.PROPERTY_CATID
                            + "' property");
                }

                if (catidFreq.containsKey(catid))
                {
                    catidFreq.put(catid, new Integer(((Integer) catidFreq
                        .get(catid)).intValue() + 1));
                }
                else
                {
                    catidFreq.put(catid, new Integer(1));
                }
            }

            // Find the predominant catid. In case of a draw, the cluster
            // does not have any catid.
            // TODO: introduce some contamination cut-off here?
            String clusterCatid = null;
            int maxFreq = 0;
            int clusterCount = 0;
            for (Iterator freqIter = catidFreq.keySet().iterator(); freqIter
                .hasNext();)
            {
                String catid = (String) freqIter.next();
                int freq = ((Integer) catidFreq.get(catid)).intValue();

                if (freq > maxFreq)
                {
                    maxFreq = freq;
                    clusterCount = 1;
                    clusterCatid = catid;
                }
                else if (freq == maxFreq)
                {
                    clusterCount++;
                }
            }

            if (clusterCount == 1)
            {
                rawCluster.setProperty(PROPERTY_CLUSTER_PREDOMINANT_CATID,
                    clusterCatid);
            }
        }
    }
}