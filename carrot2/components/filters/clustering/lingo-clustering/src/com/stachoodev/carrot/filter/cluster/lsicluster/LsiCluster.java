

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


package com.stachoodev.carrot.filter.cluster.lsicluster;


import com.stachoodev.carrot.filter.cluster.common.Cluster;
import com.stachoodev.carrot.filter.cluster.common.Snippet;


/**
 * @author stachoo
 */
public class LsiCluster
    extends Cluster
{
    /** */
    private int [] primaryFeatures;

    /**
     *
     */
    public LsiCluster()
    {
        this((Snippet []) null);
    }


    /**
     * @param snippets
     */
    public LsiCluster(Snippet [] snippets)
    {
        this(snippets, (String []) null);
    }


    /**
     * @param snippets
     * @param labels
     */
    public LsiCluster(Snippet [] snippets, String [] labels)
    {
        this(snippets, null, labels);
    }


    /**
     * @param snippets
     * @param clusters
     */
    public LsiCluster(Snippet [] snippets, Cluster [] clusters)
    {
        this(snippets, clusters, null);
    }


    /**
     * @param snippets
     * @param clusters
     * @param labels
     */
    public LsiCluster(Snippet [] snippets, Cluster [] clusters, String [] labels)
    {
        super(snippets, clusters, labels);
    }


    public LsiCluster(Cluster [] clusters)
    {
        this(null, clusters, null);
    }

    /**
     * @return int[]
     */
    public int [] getPrimaryFeatures()
    {
        return primaryFeatures;
    }


    /**
     * Sets the primaryFeatures.
     *
     * @param primaryFeatures The primaryFeatures to set
     */
    public void setPrimaryFeatures(int [] primaryFeatures)
    {
        this.primaryFeatures = primaryFeatures;
    }


    public LsiCluster [] getLsiClusters()
    {
        return (LsiCluster []) clusters.toArray(new LsiCluster[clusters.size()]);
    }
}
