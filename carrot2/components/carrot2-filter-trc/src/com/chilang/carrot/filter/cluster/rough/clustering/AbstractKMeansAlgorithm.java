/**
 * 
 * @author chilang
 * Created 2003-12-15, 01:53:31.
 */
package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.util.Timer;

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
     * @return
     */
    public abstract int[][] getClusters();


    /**
     * Return cluster membership matrix
     * Colums = cluster membership vector
     * @return
     */
    public abstract double[][] getMembership();


    /**
     * Return cluster's representation vector as a matrix
     * Rows = vectors
     * @return
     */
    public abstract double[][] getClusterRepresentation();


    /**
     * Return indices of unclassified objects
     * @return
     */
    public abstract int[] getUnclassified();

    /**
     * Return centroid of unclassified objects
     * @return
     */
    public abstract double[] getUnclassifiedCentroid();

    /**
     * Return membership vector for unclassified objects;
     * Length of vector = number of unclassified object
     * @return
     */
    public abstract double[] getUnclassifiedMembership();

}
