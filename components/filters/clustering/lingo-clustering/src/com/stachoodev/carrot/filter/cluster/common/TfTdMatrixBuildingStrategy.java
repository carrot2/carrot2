

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
 * @author stachoo
 */
public class TfTdMatrixBuildingStrategy
    implements TdMatrixBuildingStrategy
{
    /** */
    private static final int DEFAULT_MINIMUM_TD = 2;

    /** */
    private static final int DEFAULT_MAXIMUM_SIZE = -1; // unlimited

    /** */
    private int minimumTd;
    private int maximumSize;

    /**
     *
     */
    public TfTdMatrixBuildingStrategy()
    {
        this(DEFAULT_MINIMUM_TD);
    }


    /**
     * @param minimumTd
     */
    public TfTdMatrixBuildingStrategy(int minimumTd)
    {
        this(minimumTd, DEFAULT_MAXIMUM_SIZE);
    }


    /**
     * @param minimumTd
     */
    public TfTdMatrixBuildingStrategy(int minimumTd, int maximumSize)
    {
        this.minimumTd = minimumTd;
        this.maximumSize = maximumSize;
    }

    /**
     * @see com.stachoodev.carrot.filter.cluster.common.TdMatrixBuildingStrategy#buildTdMatrix(com.stachoodev.carrot.filter.cluster.common.ClusteringContext)
     */
    public double [][] buildTdMatrix(AbstractClusteringContext clusteringContext)
    {
        Feature [] features = clusteringContext.getFeatures();

        // Determine the last feature to be considered
        int rows = 0;
        int size = 0;

        while (
            !features[rows].isStopWord() && (features[rows].getTf() >= minimumTd)
                && ((maximumSize < 1) || (size <= maximumSize))
        )
        {
            rows++;
            size += clusteringContext.getSnippets().length;
        }

        // Create TD matrix
        double [][] tdMatrix = new double[rows][clusteringContext.getSnippets().length];

        for (int term = 0; term < tdMatrix.length; term++)
        {
            int [] snippetIndices = features[term].getSnippetIndices();

            for (int doc = 0; doc < snippetIndices.length; doc++)
            {
                tdMatrix[term][snippetIndices[doc]] = features[term].getSnippetTf()[snippetIndices[doc]];
            }
        }

        return tdMatrix;
    }
}
