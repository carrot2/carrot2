

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


import com.stachoodev.util.suffixarrays.wrapper.Substring;
import java.util.Properties;


/**
 *
 */
public class ClusteringResults
{
    /** */
    private Cluster [] clusters;

    /** */
    private Substring [] keywords;

    /** */
    private Properties techInfo;

    /** */
    private double [][] termTermMatrix;

    /**
     * Method ClusteringResults.
     *
     * @param clusters
     */
    public ClusteringResults(Cluster [] clusters)
    {
        this(clusters, null);
    }


    /**
     * Method ClusteringResults.
     *
     * @param keywords
     */
    public ClusteringResults(Substring [] keywords)
    {
        this(null, keywords);
    }


    /**
     * Method ClusteringResults.
     *
     * @param keywords
     */
    public ClusteringResults(Cluster [] clusters, Substring [] keywords)
    {
        this.keywords = keywords;
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


    /**
     * Returns the keywords.
     *
     * @return Substring[]
     */
    public Substring [] getKeywords()
    {
        return keywords;
    }


    /**
     * Returns the techInfo.
     *
     * @return Properties
     */
    public Properties getTechInfo()
    {
        return techInfo;
    }


    /**
     * Sets the techInfo.
     *
     * @param techInfo The techInfo to set
     */
    public void setTechInfo(Properties techInfo)
    {
        this.techInfo = techInfo;
    }


    /**
     * Returns the termTermMatrix.
     *
     * @return double[][]
     */
    public double [][] getTermTermMatrix()
    {
        return termTermMatrix;
    }


    /**
     * Sets the termTermMatrix.
     *
     * @param termTermMatrix The termTermMatrix to set
     */
    public void setTermTermMatrix(double [][] termTermMatrix)
    {
        this.termTermMatrix = termTermMatrix;
    }
}
