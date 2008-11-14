
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

import cern.colt.function.DoubleFunction;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.doublealgo.Statistic;
import cern.jet.math.Functions;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.measure.Similarity;
import org.carrot2.filter.trc.util.MatrixUtils;


/**
 * Generic implementation of soft K-Means algorithm with hard assignment
 */
public class SoftKMeansAlgorithm extends AbstractKMeansAlgorithm {


    /**
     * Objects matrix. (n * m)
     * rows = objects; columns = object features
     */
    DoubleMatrix2D objectMatrix;
    /**
     * Membership matrix (n-object * k-clusters)
     * row = object, column = cluster
     */
    DoubleMatrix2D membership;
    /**
     * Clusters representation matrix (k-clusters * m-features)
     * Row = cluster
     */
    DoubleMatrix2D clusters;

    //store last membership
    //use to calculate changes
    DoubleMatrix2D lastMembership;

    int noOfObjects;
    int noOfFeatures;
    int noOfClusters;

    double membershipThreshold = 0.8;
    double minimumOverallChanges = 0.1;

    Similarity similarity;
    Statistic.VectorVectorFunction distance;
    int iteration = 0;


    /**
     * Function that return 1 if given argument is over given threshold, 0 otherwise
     */
    private MatrixUtils.DoubleToIntFunction thresholdFilter;

    /**
     * Construct a soft clustering algorithm
     * @param vectors vectors to be clustered (row = vector)
     * @param k number of cluster
     * @param clusterMembershipThreshold
     * @param similarityMeasure
     */
    public SoftKMeansAlgorithm(double[][] vectors,
                                      int k,
                                      double clusterMembershipThreshold,
                                      Similarity similarityMeasure) {
        similarity = similarityMeasure;
        noOfClusters = k;
        membershipThreshold = clusterMembershipThreshold;
        noOfObjects = vectors.length;
        noOfFeatures = vectors[0].length;
        objectMatrix = DoubleFactory2D.sparse.make(vectors);
//        membership = DoubleFactory2D.dense.make(noOfObjects, noOfFeatures);
        distance = new Statistic.VectorVectorFunction() {
            public double apply(DoubleMatrix1D x, DoubleMatrix1D y) {
                return similarity.measure(x.toArray(), y.toArray());
            }
        };
        thresholdFilter = new ThresholdBinaryFilter(membershipThreshold);
    }

    protected void prepareInitialClusters() {

        //randomly select k objects
        int[] initialClusters = generateInitialDocuments();

//        System.out.println("Initial cluster indices : " + ArrayUtils.toString(initialClusters));
        //clusters = selected k rows from objects matrix
        clusters = objectMatrix.viewSelection(initialClusters, null).copy();
        membership = DoubleFactory2D.dense.make(noOfObjects, noOfClusters);
        lastMembership = membership;
//        System.out.println("Initial : "+clusters);
    }

    private int[] generateInitialDocuments() {
//        return ArrayUtils.randomIntArray(noOfClusters, noOfObjects);
        // choose every step-th document as a centroid
        int step = noOfObjects / noOfClusters;
        int[] initial = new int[noOfClusters];
        int index = 0;
        for (int i = 0; i < initial.length; i++) {
            initial[i] = index;
            index += step;
        }
        return initial;
    }

    protected void doClustering() {
        iteration++;
        lastMembership = membership.copy();
        membership = MatrixUtils.distance(objectMatrix, clusters, distance);
//        System.out.println("iteration: " + iteration);
//        System.out.println("member   : "+membership);
    }




    protected void calculateClusterRepresentation() {
        //calculate cluster representation as centroid
        //filter out low membership values
        //remaining values indicate cluster membership
        membership.assign(filterFunction);
//        System.out.println("filter   : "+membership);
        for (int i = noOfClusters; --i >= 0;) {
            //indices of objects belonging into i cluster
            IntArrayList nonzeroes = new IntArrayList();
            membership.viewColumn(i).getNonZeros(nonzeroes, null);
            nonzeroes.trimToSize();
//            System.out.println(" cluster " + i + " :" + nonzeroes);
            DoubleMatrix1D centroid = createCentroid(nonzeroes.elements());
            clusters.viewRow(i).assign(centroid);
        }

//        System.out.println("clusters : " + clusters);

    }

    /**
     * Create centroid from vector indices
     * @param vectorIndices
     */
    private DoubleMatrix1D createCentroid(int[] vectorIndices) {
        DoubleMatrix2D clusterObjects = objectMatrix.viewSelection(vectorIndices, null);
        DoubleMatrix1D centroid = MatrixUtils.apply(clusterObjects, MatrixUtils.average);
        return centroid;
    }

    //filter function that zero values under given threshold
    private DoubleFunction filterFunction = new DoubleFunction() {
        public double apply(double argument) {
            return argument > membershipThreshold ? argument : 0;
        }
    };


    protected boolean stopCondition() {

        //changes = abs(lastMembership - membership)
        DoubleMatrix2D changes = lastMembership.assign(membership, Functions.chain(Functions.abs, Functions.minus));
        //if overall changes small enough - STOP
        double overallChanges = changes.zSum();
//        System.out.println("Changes " + overallChanges);
        return overallChanges < minimumOverallChanges;
    }

    /**
     * Filter values over given threshold.
     * Application return 1 if over threshold, 0 otherwise
     */
    private static class ThresholdBinaryFilter implements MatrixUtils.DoubleToIntFunction {
        double threshold;

        ThresholdBinaryFilter(double t) {
            threshold = t;
        }

        public int apply(double value) {
            return value > threshold ? 1 : 0;
        }
    };

    public int[][] getClusters() {
        //create binary matrix from membership matrix,
        //cells with value over given threshold are set (to 1)
        //cell[i,j] = 1 <=> cluster i contains object j
        return MatrixUtils.convert(membership.toArray(), thresholdFilter);
    }


    public double[][] getMembership() {
        return membership.toArray();
    }

    public double[][] getClusterRepresentation() {
        return clusters.toArray();
    }

    public int[] getUnclassified() {
        int[] unclassified = new int[noOfObjects];
        int index = 0;
        for (int i = 0; i < noOfObjects; i++) {
            if (membership.viewRow(i).cardinality() == 0)
                unclassified[index++] = i;
        }
        if (index < noOfObjects) {
            int[] tmp = new int[index];
            System.arraycopy(unclassified, 0, tmp, 0, index);
            return tmp;
        }
        return unclassified;
    }

    public double[] getUnclassifiedCentroid() {
        return createCentroid(getUnclassified()).toArray();
    }

    public double[] getUnclassifiedMembership() {
        return MatrixUtils.distance(
                objectMatrix.viewSelection(getUnclassified(), null),
                DoubleFactory2D.dense.make(new double[][]{getUnclassifiedCentroid()}),
                distance).viewDice().toArray()[0];
    }
    
}
