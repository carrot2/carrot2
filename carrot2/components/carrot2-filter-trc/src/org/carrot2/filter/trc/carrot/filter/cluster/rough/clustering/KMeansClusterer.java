
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

import cern.colt.bitvector.BitVector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.FeatureVector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.SparseFeatureVector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.IRContext;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.Term;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.measure.Similarity;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.trsm.RoughSpace;

import java.util.HashMap;
import java.util.Map;


public class KMeansClusterer extends AbstractKMeansClusterer {

    public static final double TERM_SIGNIFICANCE_THRESHOLD = 0.5;

    /**
     * Threshold of (term's document frequency in cluster / no of document in cluster) above which term is
     * chosen as cluster representative
     */
    private double termSignificanceThreshold = TERM_SIGNIFICANCE_THRESHOLD;

    RoughSpace roughSpace;

    protected boolean doPostProcessing = false;

    public KMeansClusterer(int initialClusters, IRContext corpus,
                           Similarity distance, RoughSpace space) {
        super(initialClusters, corpus, distance);
        this.roughSpace = space;
    }

    public KMeansClusterer(int initialClusters, IRContext corpus, Similarity distance, RoughSpace space,
                           int maxIteration, double similarityThreshold, double termSignificance, boolean postProcessing) {
        super(initialClusters, corpus, distance);

        this.maxIteration = maxIteration;
        this.clusterSimilarityThreshold = similarityThreshold;
        this.termSignificanceThreshold = termSignificance;
        this.roughSpace = space;
        this.doPostProcessing = postProcessing;
    }

    protected Cluster[] clustering(Cluster[] currentClusters, Object[] objects) {
        log.debug("Iteration " + iteration);


        unclassifiedObjects.clear();
        unclassifiedObjects.not();

        double[][] clusterMembership = new double[noOfInitialClusters][objects.length];

        int size = objects.length;

        for (int i = 0; i < noOfInitialClusters; i++) {
            //cleanup cluster members
            currentClusters[i].getMembership().clear();
        }

        //start clustering
        for (int j = 0; j < size; j++) {
            Clusterable obj = (Clusterable) objects[j];

            //use upper approximation of document instead
            Clusterable upperRep = getUpperApproximation(obj);

            double maxSimilarity = Double.MIN_VALUE;
            int closestCluster = 0;
            //try to assign object to clusters
            //allow overlapping clusters (object can be be assigned to multiple clusters)
            for (int i = 0; i < noOfInitialClusters; i++) {
                double similarity =
                        metric.measure(currentClusters[i].getRepresentative(), upperRep);

                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    closestCluster = i;
                }
                /*if (similarity > clusterSimilarityThreshold) {
                //assign object with given similarity/membership to cluster
                clusterMembership[i][obj.getIdentifier()] = similarity;
                unclassifiedObjects.putQuick(obj.getIdentifier(), false);
                }*/
            }
            if (maxSimilarity > clusterSimilarityThreshold) {
                clusterMembership[closestCluster][obj.getIdentifier()] = maxSimilarity;
                unclassifiedObjects.putQuick(obj.getIdentifier(), false);
            }
        }

        //set membership vectors
        for (int i = 0; i < noOfInitialClusters; i++) {
            currentClusters[i].setMembership(new SparseFeatureVector(clusterMembership[i]));
        }

        //re-calculate cluster representatives

        findClusterRepresentatives(currentClusters, (Clusterable[]) objects);

        calculateClusterSimilarities(currentClusters);

        log.debug("STEP 2. Assign documents to cluster.." + timer.elapsedAsStringAndStart());
        log.debug("Unclassified=" + unclassifiedObjects.cardinality());
        return currentClusters;
    }

//    protected void calculateClusterSimilarities(Cluster[] clusters) {
//        int length = clusters.length;
//        for (int i = 0; i < length; i++) {
//            for (int j = 0; j < length; j++) {
//                if (i != j) {
//                    double similarity = metric.measure(clusters[i].getRepresentative(),clusters[j].getRepresentative());
//                    if (similarity > 0)
//                        System.out.println(i+"-"+j+" "+similarity);
//                }
//
//            }
//        }
//    }
    private Clusterable getUpperApproximation(Clusterable obj) {

//        BitVector features = obj.getFeatures().asBitVector();
//        System.out.println("obj "+obj.getIdentifier() + " bit "+features);
//        BitVector upperBitVector = (BitVector) roughSpace.upperApproximation(features);
//        System.out.println("upper bit "+upperBitVector);
//        FeatureVector upperFeatures = new SparseFeatureVector(upperBitVector, new double[upperBitVector.size()]);

//        upperFeatures = corpus.recalculateWeightUpper(upperFeatures, obj);

//        Clusterable upperRep = new ClusterRepresentative(upperFeatures);

//        return upperRep;
        return  new ClusterRepresentative((FeatureVector)roughSpace.getWeightedUpperApproximation(obj.getIdentifier()));
    }


    private void findClusterRepresentatives(Cluster[] currentClusters, Clusterable[] objects) {
        log.debug("STEP 3. Redetermine cluster representative ..");
        double[][] termWeight = corpus.getTermWeight();
        int[][] tf = corpus.getTermFrequency();
        int[] maxWeightIndices = corpus.getMaxWeightIndices();

        int termDim = termWeight[0].length;

        int noOfClusters = currentClusters.length;

        overallChanges = 0;
        for (int i = 0; i < noOfClusters; i++) {

            //get indices of documents in cluster
            int[] docIndices = currentClusters[i].getMembership().getNonZeroIndices();

            int clusterSize = docIndices.length;

            //cluster document frequency term in all document belonging to cluster
            int[] localDF = new int[termDim];
            //aggreated weight of term in all document belonging to cluster
            double[] aggregatedWeight = new double[termDim];

            for (int j = 0; j < clusterSize; j++) {
                for (int k = 0; k < termDim; k++) {
                    //count document freqency
                    if (tf[docIndices[j]][k] > 0)
                        localDF[k]++;
                    aggregatedWeight[k] += termWeight[docIndices[j]][k];
                }
            }

            //cluster representative vector; term i belongs to representative if bit i is set
            BitVector representatives = new BitVector(termDim);

            for (int j = 0; j < termDim; j++) {
                double termCoverage = (double) localDF[j] / (double) clusterSize;
                if (termCoverage > termSignificanceThreshold)
                    representatives.putQuick(j, true);
            }
            //for document in cluster that doesn't share any term with representative
            for (int j = 0; j < clusterSize; j++) {
                FeatureVector features = objects[docIndices[j]].getFeatures();
                BitVector featureBits = features.asBitVector().copy();
                //AND bit vectors of given document and representative
                featureBits.and(representatives);
                //two bit vector shares nothing, add to representative term with max weight in given document
                if (featureBits.cardinality() == 0)
                    representatives.putQuick(maxWeightIndices[docIndices[j]], true);
            }

            double[] representativeWeight = new double[termDim];
            for (int j = 0; j < termDim; j++) {

                //set weight for term representative as its aggregated weight in cluster divided
                // by its cluster's document frequency
                if (representatives.getQuick(j))
                    representativeWeight[j] = aggregatedWeight[j] / localDF[j];

            }
            //normalizeTermWeight by vector's length
            FeatureVector representativeVector = new SparseFeatureVector(representatives, representativeWeight);
//            representativeVector.normalize();

            overallChanges += diff(currentClusters[i].getRepresentative().getFeatures(), representativeVector);

            //assign new representative for cluster
            currentClusters[i].getRepresentative().setFeatures(representativeVector);
//            System.out.println("c["+i+"] "+representativeVector);
        }
//        System.out.println("Overall changes\t" + overallChanges);
    }

    protected void findClusterDescription(Cluster[] currentClusters, Clusterable[] objects) {
        log.debug("Find cluster description ..");
        double[][] termWeight = corpus.getTermWeight();
        int[][] tf = corpus.getTermFrequency();
        int[] df = corpus.getDocumentFrequency();
        int termDim = termWeight[0].length;
        int docDim = termWeight.length;

        int noOfClusters = currentClusters.length;

        //find most frequent term
        for (int i = 0; i < noOfClusters; i++) {

            //get indices of documents in cluster
            int[] docIndices = currentClusters[i].getMembership().getNonZeroIndices();

            int clusterSize = docIndices.length;

            //cluster term frequency in all document belonging to cluster
            int[] localTF = new int[termDim];

            for (int j = 0; j < clusterSize; j++) {
                for (int k = 0; k < termDim; k++) {
                    //count term freqency
                    localTF[k] += tf[docIndices[j]][k];
                }
            }

            double[] representativeWeight = new double[termDim];
            for (int j = 0; j < termDim; j++) {

                //weight = local term frequency
//                representativeWeight[j] = localTF[j];

                //choose term that is frequent in cluster but not frequent in the whole corpus
                representativeWeight[j] = localTF[j] * Math.log(docDim/df[j]);


            }

            //normalizeTermWeight by vector's length
            FeatureVector representativeVector = new SparseFeatureVector(representativeWeight);
            representativeVector.normalize();

            //assign new representative for cluster
            currentClusters[i].getRepresentative().setFeatures(representativeVector);
//            System.out.println("c["+i+"] "+representativeVector);
        }
    }

    public Cluster[] postProcessing(Cluster[] clusters, Object[] objects) {


        timer.start();
        log.debug("Post processing ..");

        //classify unassigned document using NN
        if (doPostProcessing) {

//            doKNN(objects, clusters);
//            clusters = createOtherTopicCluster(clusters, objects);
//            findClusterDescription(clusters, (Clusterable[])objects);
            findClusterDescriptionUsingPhrases(clusters, (Clusterable[])objects);
        }

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

    /**
     * Assign unclassified object to closest neighbour's cluster
     * @param objects
     * @param clusters
     */
    private void doKNN(Object[] objects, Cluster[] clusters) {
        log.debug("Do KNN ..");
        BitVector unassigned = new BitVector(objects.length);
        unassigned.clear();

        //store id of nearest neighbour for object i
        int length = objects.length;
        int[] nearestNeighbour = new int[length];

        for (int i = 0; i < length; i++) {
            nearestNeighbour[i] = -1;
        }

        for (int objectId = 0; objectId < length; objectId++) {
            if (unclassifiedObjects.getQuick(objectId)) {
                Clusterable unassignedDoc = (Clusterable) objects[objectId];
                double mostSimilar = Double.MIN_VALUE;

                //calculate measure between given object and
                // any other objects that has been classified (has cluster)
                //TODO can be optimized
                for (int j = 0; j < length; j++) {
                    if (!unclassifiedObjects.getQuick(j)) {
                        double similarity = metric.measure(unassignedDoc, (Clusterable) objects[j]);
                        if (similarity > mostSimilar && (similarity != 0.0)) {
                            mostSimilar = similarity;
                            nearestNeighbour[objectId] = j;
                        }
                    }
                }

                if (nearestNeighbour[objectId] == -1) {
                    //NN could not be found, all measure = 0
                    unassigned.putQuick(objectId, true);
                } else {
                    //calculate measure between upper approx. of given doc and its NN

                    //use upper approximation of document instead
                    Clusterable unassignedUpper = getUpperApproximation(unassignedDoc);
                    Clusterable nnUpper = getUpperApproximation((Clusterable) objects[nearestNeighbour[objectId]]);

                    double similarity = metric.measure(unassignedUpper, nnUpper);

                    //TODO double similarity = nnClusterMembership * metric.measure(unassignedUpper, nnUpper);

                    //nearest neighbor could belongs to multiple cluster
                    //assign to cluster with strongest membershipp
                    int strongestCluster = findStrongestMembershipCluster(clusters, nearestNeighbour[objectId]);

                    clusters[strongestCluster].getMembership().setWeight(objectId, similarity);


                }
            }
        }
        //redetermine cluster representative
        //        timer.start();
        findClusterRepresentatives(clusters, (Clusterable[]) objects);

        unclassifiedObjects = unassigned;
    }


    /**
     * Find cluster that given object has strongest membership with
     * @param clusters set of clusters
     * @param nearestNeighbour id of object
     * @return id of cluster with which object has strongest membership
     */
    private int findStrongestMembershipCluster(Cluster[] clusters, int nearestNeighbour) {
        int strongestCluster = 0;
        double maxMembership = Double.MIN_VALUE;

        for (int k = 0; k < noOfInitialClusters; k++) {
            double membership = clusters[k].getMembership().getWeight(nearestNeighbour);
            if (membership > 0 && membership > maxMembership) {
                strongestCluster = k;
                maxMembership = membership;
            }
        }
        return strongestCluster;
    }


    protected boolean stopCondition() {
        return (++iteration > maxIteration || fuzzyEquals(0.0, overallChanges, 0.0001));
    }
}
