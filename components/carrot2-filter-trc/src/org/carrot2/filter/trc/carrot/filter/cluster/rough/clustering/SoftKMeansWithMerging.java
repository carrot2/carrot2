
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

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import org.carrot2.filter.trc.carrot.filter.cluster.InterDocumentSimilarity;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.measure.Similarity;

public class SoftKMeansWithMerging extends SoftKMeansAlgorithm {

    double mergingThreshold;



    public SoftKMeansWithMerging(double[][] vectors, int k, double clusterMembershipThreshold, Similarity similarityMeasure, double mergingThreshold) {
        super(vectors, k, clusterMembershipThreshold, similarityMeasure);
        this.mergingThreshold = mergingThreshold;
    }


    /**
     * Merge similar clusters to prevent near-duplicated clusters
     */
    protected void mergeClusters() {
//        System.out.println("Merging");
        ClusterMergingStrategy mergingStrategy = new ClusterMergingStrategy();
        clusters = mergingStrategy.mergeClusters();
        noOfClusters = clusters.rows();
//        System.out.println("New number of cluster "+noOfClusters);
        doClustering();
        calculateClusterRepresentation();
    }


    public void cluster() {
        super.cluster();
        mergeClusters();
    }

    class ClusterMergingStrategy {

        /**
         * Map (cluster old index, index of merged cluster in new clusters array)
         * E.g. (1, 3) were merged and the results put in the 2 position in the new clusters array
         * thetn index[1] = index[3] = 2
         * index[i] = 0 means i has not changed
         */
        int[] newIndex;


        /**
         * Holds new clusters
         */
        DoubleMatrix1D[] newClusters;

        private static final int UNCHANGED = -1;

        int mergeCount;

        public ClusterMergingStrategy() {
            newIndex = new int[noOfClusters];
            for (int i = 0; i < newIndex.length; i++) {
                newIndex[i] = UNCHANGED;

            }
            newClusters = new DoubleMatrix1D[noOfClusters];
            mergeCount = 0;
        }

        /**
         * Merge cluster more similar than given threshold
         */
        public DoubleMatrix2D mergeClusters() {
            InterDocumentSimilarity interClusterSimilarity =
                    new InterDocumentSimilarity(clusters.toArray(), similarity);
            int[][] similar = interClusterSimilarity.getSimilarOver(mergingThreshold);



            for (int i = 0; i < similar.length; i++) {

                //merge two cluster and store in new cluster array
                int[] c = similar[i];
                int updatedIndex = getMergedIndex(c[0], c[1]);
//                System.out.println(c[0]+" + "+c[1] +" = "+updatedIndex);

                //if both cluster has already been part of a merge, do NOTHING
                if (updatedIndex == UNCHANGED)
                    continue;


                newClusters[updatedIndex] =
                        mergeVector(getCurrentCluster(c[0]), getCurrentCluster(c[1]));


                newIndex[c[0]] = updatedIndex;
                newIndex[c[1]] = updatedIndex;

            }


//            System.out.println(ArrayUtils.toString(newIndex));
            //copy unchanged clusters
            int lastIndex = mergeCount;
            for (int i = 0; i < newIndex.length; i++) {
                if (newIndex[i] == UNCHANGED) {
                    newClusters[lastIndex++] = clusters.viewRow(i);
                }
            }

            //copy new cluster to proper size merged clusters
            DoubleMatrix2D mergedClusters = DoubleFactory2D.dense.make(lastIndex, noOfFeatures);
            for (int i = 0; i < lastIndex; i++) {
                mergedClusters.viewRow(i).assign(newClusters[i]);
            }
            return mergedClusters;
        }

        /**
         * Return new index for cluster being a merge of a and b as follows :
         *   -  if a AND b both has been previously part of a merge
         *      then do nothing (by returning UNCHANGED)
         *   -  if a OR b has been previously part of a merge
         *      then return previously merged index
         *   -  if a AND b has NOT been merge then return new index and increment it
         *
         * @param a
         * @param b
         */
        private int getMergedIndex(int a, int b) {
            //a,b has not been previously a part of merge
            if ((newIndex[a] == UNCHANGED) && (newIndex[b] == UNCHANGED)) {
                return mergeCount++;
            }
            //return index of a previously merged
            //
            if (newIndex[a] != UNCHANGED) {
                //a has been merged
                if (newIndex[b] != UNCHANGED) {
                    //both has been merged
                    return UNCHANGED;
                } else {
                    return newIndex[a];
                }
            }
            // or else b must has been previosly merged
            return newIndex[b];
        }

        private DoubleMatrix1D getCurrentCluster(int index) {
            if (newIndex[index] == UNCHANGED) {
                //cluster untouched
                return clusters.viewRow(index);
            } else {
                //cluster has been merged and has a new index
                return newClusters[newIndex[index]];
            }
        }

        private DoubleMatrix1D mergeVector(DoubleMatrix1D a, DoubleMatrix1D b) {
            DoubleMatrix1D merged = a.copy();
            merged.assign(b, Functions.chain(Functions.div(2), Functions.plus));
            return merged;
        }


    }


}
