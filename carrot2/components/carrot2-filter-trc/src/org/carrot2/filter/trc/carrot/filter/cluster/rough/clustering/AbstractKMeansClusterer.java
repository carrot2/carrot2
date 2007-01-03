
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
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram.NGram;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram.NGramCollector;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.measure.Similarity;
import org.carrot2.filter.trc.util.*;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.SortedMap;

public abstract class AbstractKMeansClusterer extends AbstractClusterer{

    protected final org.carrot2.filter.trc.util.Timer timer = new org.carrot2.filter.trc.util.Timer();
    protected BitVector unclassifiedObjects;
    /**
     * Number of initial clusters
     */
    protected int noOfInitialClusters;
    protected static final Logger log = Logger.getLogger(KMeansBaselineClusterer.class);
    protected Similarity metric;
    protected double overallChanges = Double.MAX_VALUE;
    protected int nterm;
    protected int maxIteration = KMeansClusterer.MAX_ITERATION;
    protected int iteration = 1;
    protected IRContext corpus;
    public static final double CLUSTER_SIMILARITY_THRESHOLD = 0.225;
    public static final int MAX_ITERATION = 3;
    /**
     * Threshold for similarity above which document is assigned to cluster
     */
    protected double clusterSimilarityThreshold = CLUSTER_SIMILARITY_THRESHOLD;


    protected AbstractKMeansClusterer(int noOfClusters, IRContext context, Similarity distance) {
        this.noOfInitialClusters = noOfClusters;
        this.corpus = context;
        this.metric = distance;
        this.nterm = corpus.noOfTerms();
    }

    /**
     * Calculate internal similarity of cluster
     * @param clusters
     */
    protected void calculateClusterSimilarities(Cluster[] clusters) {

        //calculate internal similarity of cluster as
        // dot product of cluster's centroid vector
        int length = clusters.length;
        double aggrSim = 0;
        double[][] td = corpus.getTermWeight();
        int[] clusterSize = new int[length];
        for (int i = 0; i < length; i++) {
            int[] nonzeroes = clusters[i].getMembership().getNonZeroIndices();
            clusterSize[i] = nonzeroes.length;
//            FeatureVector centroid = clusters[i].getRepresentative().getFeatures();

//                    centroid.getNonZeroIndices();
            double sim = 0;
            int nzl = nonzeroes.length;
            for (int j = 0; j < nzl; j++) {
                for (int k=j; k < nzl; k++) {


//                double value =
//                        centroid.getWeight(nonzeroes[j]);
                sim += metric.measure(td[nonzeroes[j]], td[nonzeroes[k]]);
                }
            }
//            System.out.println("Cluster "+i+" : "+sim);
            aggrSim += sim;
//            System.out.println(i+" - "+sim);
//            System.out.println(centroid.asBitVector());
        }
//        System.out.println("Cluster size\t"+ArrayUtils.toString(clusterSize));
//        System.out.println("Overall similarity\t"+aggrSim);
    }

    public Cluster[] initialization(Object[] objects) {
        timer.start();

//        this.objects = objects;
        this.unclassifiedObjects = new BitVector(objects.length);
        clusters = new Cluster[noOfInitialClusters];
        int[] randomIndices = ArrayUtils.randomIntArray(noOfInitialClusters, objects.length);

        int length = randomIndices.length;
        for (int i = 0; i < length; i++) {
            //create initial cluster based on
            // representative generated from randomly selected documents
            clusters[i] = new SimpleCluster(generateRepresentative((Clusterable)objects[randomIndices[i]]));
        }
        KMeansBaselineClusterer.log.debug("Init clusters.. "+timer.elapsedAsStringAndStart());
        return clusters;
    }

    protected Clusterable generateRepresentative(Clusterable clusterable) {
        return new ClusterRepresentative(clusterable.getFeatures().copy());
    }

    protected double calculateCentroidChange(FeatureVector oldCentroid, FeatureVector newCentroid) {
        return diff(oldCentroid, newCentroid);
//                1.0 - metric.measure(new ClusterRepresentative(oldCentroid), new ClusterRepresentative(newCentroid));
//        System.out.println("centroid change "+measure);
    }

    protected double diff(FeatureVector v1, FeatureVector v2) {
        double d = 0;
        for (int i=v1.size(); --i >= 0; ) {
            d += Math.abs(v1.getWeight(i) - v2.getWeight(i));
        }
        return d;
    }

    protected boolean fuzzyEquals(double expected, double value, double delta) {
        return Math.abs(expected - value) < delta;
    }

    public BitVector getUnclassifiedObjects() {
        return unclassifiedObjects;
    }

    /**
     * Create "other" cluster for unclassified objects
     * @param objects
     * @param clusters
     */
    protected Cluster[] createOtherTopicCluster(Cluster[] clusters, Object[] objects) {
        log.debug("Create Other cluster ..");

        //store id of nearest neighbour for object i
        int length = objects.length;

        double[] membership = new double[length];
        double[] averageWeight = new double[nterm];
        int[] localDF = new int[nterm];
        for (int objectId = 0; objectId < length; objectId++) {
            if (unclassifiedObjects.getQuick(objectId)) {
                Clusterable unassignedDoc = (Clusterable) objects[objectId];
                FeatureVector feature = unassignedDoc.getFeatures();
                for(int j=0; j<nterm; j++) {
                    double weight = feature.getWeight(j);
                    if (weight > 0) {
                        averageWeight[j] += weight;
                        localDF[j]++;
                    }
                }
                //add object to cluster with membership as 1
                membership[objectId] = 1;
            }
        }
        for (int j=0; j<nterm; j++) {
            if (localDF[j] >0) {
                averageWeight[j] = averageWeight[j] / localDF[j];
            }
        }

        FeatureVector averageFeature = new SparseFeatureVector(averageWeight);
        averageFeature.normalize();

        Cluster otherCluster = new SimpleCluster(new ClusterRepresentative(averageFeature));
        otherCluster.setMembership(new SparseFeatureVector(membership));

        //form new array of cluster, adding other cluster
        Cluster[] newClusters = new Cluster[clusters.length + 1];
        System.arraycopy(clusters, 0, newClusters, 0, clusters.length);
        newClusters[clusters.length] = otherCluster;


        //every objects has been classified
        BitVector unassigned = new BitVector(objects.length);
        unassigned.clear();

        unclassifiedObjects = unassigned;
        return newClusters;
    }

    protected void findClusterDescriptionUsingPhrases(Cluster[] currentClusters, Clusterable[] objects) {
        log.debug("Find cluster description ..");
        double[][] termWeight = corpus.getTermWeight();
        int[][] tf = corpus.getTermFrequency();
        int[] df = corpus.getDocumentFrequency();
        int termDim = termWeight[0].length;
        int docDim = termWeight.length;
        String[] queryWords = corpus.getQueryWords();
        int noOfClusters = currentClusters.length;

        //find most frequent term
        for (int i = 0; i < noOfClusters; i++) {

            //get indices of documents in cluster
            int[] docIndices = currentClusters[i].getMembership().getNonZeroIndices();

            NGramCollector nGramCollector = new NGramCollector(5, corpus.getFilter());
            Snippet[] snippets = corpus.getSnippetByIndices(docIndices);
            for (int j = 0; j < snippets.length; j++) {
                nGramCollector.process(snippets[j]);
            }

            SortedMap phrasesMap = nGramCollector.getDocumentFrequency().getDescendingSorted();

            int labelsSize = 5;
            String[] labels = new String[labelsSize];
            int k = 0;
            for (Iterator iterator = phrasesMap.keySet().iterator(); iterator.hasNext() && k <labelsSize; ) {
                NGram nGram = (NGram)iterator.next();
                //choose labels amongs phrases that doesn't overlap much with query words
                if (StringUtils.overlap(nGram.getWords(), queryWords) < 1) {
                    labels[k++] = nGram.toString();
                }
            }
            currentClusters[i].setLabels(labels);
        }
    }

    protected void findMostDicriminativeFeatures(Cluster[] currentClusters, Clusterable[] objects) {
        log.debug("Find most dicriminative features ");
        double[][] termWeight = corpus.getTermWeight();
        int[][] tf = corpus.getTermFrequency();
        int[] df = corpus.getDocumentFrequency();
        int termDim = termWeight[0].length;
        int docDim = termWeight.length;

        int noOfClusters = currentClusters.length;

        //cluster's term frequency
        int[][] clusterTF = new int[noOfClusters][termDim];
        int[][] clusterDF = new int[noOfClusters][termDim];

        double[] discriminativeTermMetric = new double[termDim];
        int[] corpusTF = MatrixUtils.sumRows(tf);


        //find most discriminative term
        for (int i = 0; i < noOfClusters; i++) {

            //get indices of documents in cluster
            int[] docIndices = currentClusters[i].getMembership().getNonZeroIndices();

            clusterTF[i] = MatrixUtils.sumSelectedRows(tf, docIndices);

            clusterDF[i] = MatrixUtils.countNonZeroesFromSelectedRows(tf, docIndices);

        }
        int[] maxDF = MatrixUtils.maxByColumns(clusterDF);

        int[] maxTF = MatrixUtils.maxByColumns(clusterTF);

        Term[] terms = corpus.getTermArray();

        NumberFormat nf = FormatterFactory.getNumberFormat();
        for (int i = 0; i < discriminativeTermMetric.length; i++) {

            if (df[i] > 1 ) {
                //discriminative terms are those that
                // are more frequent in particular cluster than outside that cluster
//            discriminativeTermMetric[i] = Math.log((double)maxTF[i] * (docDim - 1)/ (double)(corpusTF[i] - maxTF[i]));
                double termDiscriminativeMetric = Math.log((double)maxTF[i] * (noOfClusters - 1)/ (double)(corpusTF[i] - maxTF[i]));
                double documentDiscriminativeMetric = Math.log((double)maxDF[i] * (noOfClusters - 1)/ (double)(df[i] - maxDF[i]));

//                System.out.println(terms[i].getOriginalTerm()+"\t"+nf.format(documentDiscriminativeMetric)+"\t"+nf.format(termDiscriminativeMetric));
            }/* else {
                System.out.println(terms[i].getOriginalTerm());
            }*/
        }


//        for (int i = 0; i < terms.length; i++) {
//            Term term = terms[i];
//            System.out.println(term.getOriginalTerm() + "\t" + discriminativeTermMetric[i]);
//        }
    }
}
