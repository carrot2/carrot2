

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.carrot.filter.cluster.common;

/**
 * @author Stanislaw Osinski
 */
public class ClusteringResults
{
    private Cluster [] clusters;

    /**
     * Method ClusteringResults.
     *
     * @param clusters
     */
    public ClusteringResults(Cluster [] clusters)
    {
        this.clusters = clusters;
    }

    /**
     * Returns the clusters.
     *
     * @return Cluster[]
     */
    public Cluster [] getClusters()
    {
        return clusters;
    }

}
