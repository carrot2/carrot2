
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

import org.carrot2.filter.trc.util.Timer;

/**
 * A boiler-plate implementation of generic K-Means algorithm.
 * Soft (fuzzy) membership allowed.
 */
public abstract class AbstractKMeansAlgorithm {

    protected static final Timer timer = new Timer();

    /**
     * Prepare initial clusters
     */
    protected abstract void prepareInitialClusters();

    /**
     * Main step in clustering algorithm : assign objects to clusters
     */
    protected abstract void doClustering();

    /**
     * Calculate representation for cluster (usually in form of a centroid)
     */
    protected abstract void calculateClusterRepresentation();


    /**
     * Stop condition for algorithm
     * @return <code>true</code> when algorithm should stop processing
     */
    protected abstract boolean stopCondition();

    /**
     * Execute clustering
     */
    public void cluster() {

        timer.start();
        /**
         * General scheme of clustering algorithm
         */
        prepareInitialClusters();
        do {
            doClustering();
            calculateClusterRepresentation();
        } while (!stopCondition());

//        System.out.println("Elapsed : "+timer.elapsedAsString());
    }

    /**
     * Return clusters as a form of binary matrix
     * (n rows-objects * k colums-clusters)
     * Cell[i,j] = 1 iff object i belongs to cluster j
     */
    public abstract int[][] getClusters();


    /**
     * Return cluster membership matrix
     * Colums = cluster membership vector
     */
    public abstract double[][] getMembership();


    /**
     * Return cluster's representation vector as a matrix
     * Rows = vectors
     */
    public abstract double[][] getClusterRepresentation();


    /**
     * Return indices of unclassified objects
     */
    public abstract int[] getUnclassified();

    /**
     * Return centroid of unclassified objects
     */
    public abstract double[] getUnclassifiedCentroid();

    /**
     * Return membership vector for unclassified objects;
     * Length of vector = number of unclassified object
     */
    public abstract double[] getUnclassifiedMembership();

}
