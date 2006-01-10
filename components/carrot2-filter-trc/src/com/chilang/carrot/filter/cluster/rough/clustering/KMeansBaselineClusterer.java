
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.SparseFeatureVector;
import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.data.Term;
import com.chilang.carrot.filter.cluster.rough.measure.Similarity;

import java.util.HashMap;
import java.util.Map;


public class KMeansBaselineClusterer extends AbstractKMeansClusterer {

    public KMeansBaselineClusterer(int initialClusters, IRContext corpus,
                           Similarity distance) {
       super(initialClusters, corpus, distance);
    }

    public KMeansBaselineClusterer(int initialClusters, IRContext corpus, Similarity distance,
                                   int maxIteration, double similarityThreshold) {
        super(initialClusters, corpus, distance);
        this.maxIteration = maxIteration;
        this.clusterSimilarityThreshold = similarityThreshold;
    }


    protected Cluster[] clustering(Cluster[] currentClusters, Object[] objects) {
        log.debug("Iteration " + iteration);



        unclassifiedObjects.clear();
        unclassifiedObjects.not();

        double[][] clusterMembership = new double[noOfInitialClusters][objects.length];

        int size = objects.length;

        for (int i=0; i <noOfInitialClusters; i++) {
            //cleanup cluster members
            currentClusters[i].getMembership().clear();
        }

        //start clustering
        for (int j=0; j < size; j++) {
            Clusterable obj = (Clusterable)objects[j];

            double maxSimilarity = Double.MIN_VALUE;
            int closestCluster = 0;
            //try to assign object to clusters
            //allow overlapping clusters (object can be be assigned to multiple clusters)
            for (int i=0; i <noOfInitialClusters; i++) {
                double similarity =
                        metric.measure(currentClusters[i].getRepresentative(), obj);
                if (similarity > maxSimilarity) {
                    closestCluster = i;
                    maxSimilarity = similarity;
                }
//                if (similarity > clusterSimilarityThreshold) {
//                    assign object with given similarity/membership to cluster
//                    clusterMembership[i][obj.getIdentifier()] = similarity;
//                    unclassifiedObjects.putQuick(obj.getIdentifier(), false);
//                }
            }
            clusterMembership[closestCluster][obj.getIdentifier()] = maxSimilarity;
            unclassifiedObjects.putQuick(obj.getIdentifier(), false);
        }

        //set membership vectors
        for (int i=0; i <noOfInitialClusters; i++) {
            currentClusters[i].setMembership(new SparseFeatureVector(clusterMembership[i]));
        }

        //re-calculate cluster representatives

        findClusterRepresentatives(currentClusters, (Clusterable[]) objects);

        calculateClusterSimilarities(currentClusters);

        log.debug("Assign documents to cluster.." + timer.elapsedAsStringAndStart());
        log.debug("Unclassified=" + unclassifiedObjects.cardinality());
        return currentClusters;
    }



    private void findClusterRepresentatives(Cluster[] currentClusters, Clusterable[] objects) {
        log.debug("Redetermine cluster representative ..");


        /**
         * Calculate cluster representative as centroid of cluster's objects
         */
        double[][] termWeight = corpus.getTermWeight();

        int termDim =  termWeight[0].length;

        int noOfClusters = currentClusters.length;

        overallChanges = 0;
        for (int i = 0; i < noOfClusters; i++) {

            //get indices of documents in cluster
            int[] docIndices = currentClusters[i].getMembership().getNonZeroIndices();

            int clusterSize = docIndices.length;

            //aggreated weight of term in all document belonging to cluster
            double[] aggregatedWeight = new double[termDim];

            for(int j=0; j<clusterSize; j++) {
                for (int k=0; k<termDim; k++) {
                    aggregatedWeight[k] += termWeight[docIndices[j]][k];
                }
            }

            for (int j = 0; j < termDim; j++) {
                aggregatedWeight[j] /= clusterSize;

            }

            FeatureVector representativeVector = new SparseFeatureVector(aggregatedWeight);

            overallChanges += calculateCentroidChange(currentClusters[i].getRepresentative().getFeatures(), representativeVector);
            //assign new representative for cluster
            currentClusters[i].getRepresentative().setFeatures(representativeVector);
        }
//        System.out.println("Overall changes\t"+overallChanges);

    }

    public Cluster[] postProcessing(Cluster[] clusters, Object[] objects) {


        timer.start();
        log.debug("Post processing ..");


//        clusters = createOtherTopicCluster(clusters, objects);

        findClusterDescriptionUsingPhrases(clusters, (Clusterable[])objects);
        findMostDicriminativeFeatures(clusters, (Clusterable[])objects);

        //assign real documents to cluster
        int noOfClusters = clusters.length;
        for (int i = 0; i < noOfClusters; i++) {
            Snippet[] snips = corpus.getSnippetByIndices(clusters[i].getMembership().getNonZeroIndices());
            clusters[i].setMembers(snips);
            FeatureVector clusterFeatures = clusters[i].getRepresentative().getFeatures();
            Term[] terms = corpus.getTermByIndices(clusterFeatures.getNonZeroIndices());
            Map termWeight = new HashMap();
            int termSize = terms.length;
            for (int j = 0; j < termSize; j++) {
                termWeight.put(terms[j], new Double(clusterFeatures.getWeight(terms[j].getId())));
            }
            clusters[i].setRepresentativeTerm(termWeight);
        }

        log.debug("Unassigned " + unclassifiedObjects.cardinality());
        log.debug(timer.elapsedAsStringAndStart());
        return clusters;


    }


    protected boolean stopCondition() {
        return (++iteration > maxIteration || fuzzyEquals(0.0, overallChanges, 0.0001));
    }

}
