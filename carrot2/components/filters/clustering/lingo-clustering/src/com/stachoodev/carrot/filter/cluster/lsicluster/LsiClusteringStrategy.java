

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


import Jama.Matrix;
import Jama.SingularValueDecomposition;
import com.stachoodev.carrot.filter.cluster.common.*;
import com.stachoodev.util.arrays.ArrayUtils;
import com.stachoodev.util.log.TimeLogger;
import com.stachoodev.util.matrix.MatrixUtils;
import org.apache.log4j.Logger;
import java.util.*;


/**
 * Implements the LSI clustering algorithm
 */
public class LsiClusteringStrategy
    implements ClusteringStrategy
{
    /** Algorithm parameter defaults */
    protected static final double DUPLICATE_CLUSTERS_THRESHOLD = 0.300;
    protected static final double CLUSTER_ASSIGNMENT_THRESHOLD = 0.225;
    protected static final double CANDIDATE_CLUSTER_THRESHOLD = 0.775;

    /** Algorithm parameters */
    protected double duplicateClustersThreshold = DUPLICATE_CLUSTERS_THRESHOLD;
    protected double clusterAssignmentThreshold = CLUSTER_ASSIGNMENT_THRESHOLD;
    protected double candidateClusterThreshold = CANDIDATE_CLUSTER_THRESHOLD;

    /** Logger */
    protected static final Logger logger = Logger.getLogger(LsiClusteringStrategy.class);

    /** Clustering data */
    protected AbstractClusteringContext clusteringContext;

    /** Input data */
    protected Feature [] features;
    protected Snippet [] snippets;
    protected int termCount;
    protected int docCount;
    protected int nonStopTermCount;
    protected int stopTermCount;
    protected int phraseCount;
    protected int clusterCount;
    protected int candidateClusterCount;
    protected int firstPhraseIndex;

    /** Input term-document matrix */
    protected Matrix tdMatrix;

    /** SVD decomposition */
    protected Matrix U;
    protected Matrix Q;
    protected Matrix V;
    protected Matrix S;

    /** Indices of cluster candidate labels */
    protected int [] phraseIndices;
    protected int [] singleTermIndices;

    /** Scores of cluster candidate labels */
    protected double [] phraseScores;
    protected double [] singleTermScores;

    /** Indicates whether a phrase or sigle term should be used as a cluster label */
    protected boolean [] usePhrase;
    protected boolean [] useSingleTerm;

    /** Scores of candidate clusters */
    protected Cluster [] candidateClusters;
    protected double [] candidateClusterScores;

    /**
     * @see com.stachoodev.carrot.filter.cluster.common.ClusteringStrategy#cluster(com.stachoodev.carrot.filter.cluster.common.ClusteringContext)
     */
    public ClusteringResults cluster(AbstractClusteringContext clusteringContext)
    {
        this.clusteringContext = clusteringContext;

        TimeLogger timeLogger = new TimeLogger();

        // Init parameters
        Object value;
        if ( (value = clusteringContext.getParameter("lsi.threshold.clusterAssignment")) != null)
        {
            try
            {
                clusterAssignmentThreshold = Double.parseDouble(unwrapString(value));
            }
            catch (NumberFormatException e)
            {
                // Use the default
                clusterAssignmentThreshold = CLUSTER_ASSIGNMENT_THRESHOLD;
            }
        }

        if ((value = clusteringContext.getParameter("lsi.threshold.candidateCluster")) != null)
        {
            try
            {
                candidateClusterThreshold = Double.parseDouble(unwrapString(value));
            }
            catch (NumberFormatException e)
            {
                // Use the default
                candidateClusterThreshold = CANDIDATE_CLUSTER_THRESHOLD;
            }
        }

        timeLogger.start();
        prepareData();
        timeLogger.logElapsedAndStart(logger, "prepareData()");
        createReducedRepresentation();
        timeLogger.logElapsedAndStart(logger, "createReducedRepresentation()");
        describeCandidateClusters();
        timeLogger.logElapsedAndStart(logger, "describeCandidateClusters()");
        pruneCandidateClusters();
        timeLogger.logElapsedAndStart(logger, "pruneCandidateClusters()");

        Cluster [] finalClusters = createClusters();
        timeLogger.logElapsedAndStart(logger, "createClusters()");

        return new ClusteringResults(finalClusters);
    }

    /**
     * Unwraps a String out of a list, if needed.
     */
    private String unwrapString(Object value) {
        if (value instanceof List) {    
            return (String) ((List) value).get(0);
        } else {
            return (String) value;
        }
    }

    /**
     * Prepares intermediate clustering data (term-document matrix).
     */
    protected void prepareData()
    {
        snippets = clusteringContext.getPreprocessedSnippets();
        features = clusteringContext.getFeatures();

        // Count the non-stop words
        nonStopTermCount = 0;

        while ((nonStopTermCount < features.length) && !features[nonStopTermCount].isStopWord())
        {
            nonStopTermCount++;
        }

        // Count the stop-words
        stopTermCount = 0;

        while (
            ((nonStopTermCount + stopTermCount) < features.length)
                && features[nonStopTermCount + stopTermCount].isStopWord()
        )
        {
            stopTermCount++;
        }

        // Count the phrases
        phraseCount = features.length - nonStopTermCount - stopTermCount;
        firstPhraseIndex = nonStopTermCount + stopTermCount;

        // Create TD matrix
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfidfTdMatrixBuildingStrategy(
                2, 250 * 150
            );

        tdMatrix = new Matrix(tdMatrixBuildingStrategy.buildTdMatrix(clusteringContext));

        MatrixUtils.normalizeColumnLengths(tdMatrix);

        termCount = tdMatrix.getRowDimension();
        docCount = tdMatrix.getColumnDimension();
    }


    /**
     * Creates a dimension-reduced representation of term-document matrix. Presently SVD matrix
     * decomposition is used.
     */
    protected void createReducedRepresentation()
    {
        // Check dimensions
        boolean transposed = false;

        if (tdMatrix.getColumnDimension() > tdMatrix.getRowDimension())
        {
            transposed = true;
            tdMatrix = tdMatrix.transpose();
        }

        // The SVD

        SingularValueDecomposition svd = tdMatrix.svd();

        if (transposed)
        {
            V = svd.getU();
            U = svd.getV();
            tdMatrix = tdMatrix.transpose();
        }
        else
        {
            U = svd.getU();
            V = svd.getV();
        }

        double [] singularValues = svd.getSingularValues();
        double denominator = 0;

        for (int i = 0; i < singularValues.length; i++)
        {
            denominator += (singularValues[i] * singularValues[i]);
        }

        // Determine the number of pre-candidate clusters
        double numerator = 0;

        for (
            candidateClusterCount = 0; candidateClusterCount < singularValues.length;
                candidateClusterCount++
        )
        {
            numerator += (singularValues[candidateClusterCount] * singularValues[candidateClusterCount]);

            if (Math.sqrt(numerator / denominator) > candidateClusterThreshold)
            {
                break;
            }
        }

        U = U.getMatrix(0, U.getRowDimension() - 1, 0, candidateClusterCount - 1);
        V = V.getMatrix(0, V.getRowDimension() - 1, 0, candidateClusterCount - 1);
        S = svd.getS().getMatrix(0, candidateClusterCount - 1, 0, candidateClusterCount - 1);

        Q = new Matrix(U.getRowDimension(), U.getColumnDimension());
    }


    /**
     * Generates cluster label candidates along  with a set of scoring information.
     */
    protected void describeCandidateClusters()
    {
        // Find best phrases
        Matrix phraseMatrix;
        phraseIndices = new int[candidateClusterCount];
        phraseScores = new double[candidateClusterCount];
        Arrays.fill(phraseScores, -1);

        // Try to describe each candidate cluster with a phrase
        if (phraseCount > 0)
        {
            // Prepare phrase matrix
            phraseMatrix = new Matrix(termCount, features.length - firstPhraseIndex);

            for (int p = firstPhraseIndex; p < features.length; p++)
            {
                int [] featureIndices = features[p].getPhraseFeatureIndices();

                for (int f = 0; f < featureIndices.length; f++)
                {
                    if (
                        (featureIndices[f] < termCount)
                            && !features[featureIndices[f]].isQueryWord()
                    ) // skip stopwords and query words
                    {
                        phraseMatrix.set(
                            featureIndices[f], p - firstPhraseIndex,
                            phraseMatrix.get(featureIndices[f], p - firstPhraseIndex) + 1
                        );
                    }
                }

                // Multiply by the idf factor
                for (int f = 0; f < featureIndices.length; f++)
                {
                    if (featureIndices[f] < termCount)
                    {
                        phraseMatrix.set(
                            featureIndices[f], p - firstPhraseIndex,
                            phraseMatrix.get(featureIndices[f], p - firstPhraseIndex) * features[featureIndices[f]]
                            .getIdf()
                        );
                    }
                }
            }

            MatrixUtils.normalizeColumnLengths(phraseMatrix);

            Matrix cos = phraseMatrix.transpose().times(U);

            // For each candidate cluster choose the best phrase and
            // calculate the score of the phrase
            for (int c = 0; c < candidateClusterCount; c++)
            {
                phraseScores[c] = Math.abs(cos.get(0, c));
                phraseIndices[c] = 0 + firstPhraseIndex;

                for (int p = 1; p < cos.getRowDimension(); p++)
                {
                    // Penalize phrases longer than 5 words
                    double penalty = 1;

                    if (features[firstPhraseIndex + p].getLength() > 5)
                    {
                        penalty = 1 - ((features[firstPhraseIndex + p].getLength() - 5) * 0.25);
                    }

                    if ((Math.abs(cos.get(p, c)) * penalty) > phraseScores[c])
                    {
                        phraseScores[c] = Math.abs(cos.get(p, c)) * penalty;
                        phraseIndices[c] = p + firstPhraseIndex;
                    }
                }
            }
        }

        // Try to describe each candidate cluster with a single term
        singleTermIndices = new int[candidateClusterCount];
        singleTermScores = new double[candidateClusterCount];

        for (int c = 0; c < candidateClusterCount; c++)
        {
            // Find the single term
            singleTermScores[c] = Math.abs(U.get(0, c));
            singleTermIndices[c] = 0;

            for (int t = 1; t < U.getRowDimension(); t++)
            {
                if (Math.abs(U.get(t, c)) > singleTermScores[c])
                {
                    singleTermScores[c] = Math.abs(U.get(t, c));
                    singleTermIndices[c] = t;
                }
            }
        }

        // Take the phrase or the single word ?
        usePhrase = new boolean[candidateClusterCount];
        useSingleTerm = new boolean[candidateClusterCount];
        candidateClusterScores = new double[candidateClusterCount];

        for (int c = 0; c < candidateClusterCount; c++)
        {
            // Adjust scores
            double scale = 1.1;

            if (
                ArrayUtils.contains(
                        features[phraseIndices[c]].getPhraseFeatureIndices(), singleTermIndices[c]
                    )
            )
            {
                scale = 1.2;
            }

            // Choose
            if (singleTermScores[c] > (phraseScores[c] * scale))
            {
                useSingleTerm[c] = true;
                candidateClusterScores[c] = singleTermScores[c];
            }
            else
            {
                usePhrase[c] = true;
                candidateClusterScores[c] = phraseScores[c];
            }
        }
    }


    /**
     * Removes duplicated and too similar cluster label candidates.
     */
    protected void pruneCandidateClusters()
    {
        boolean [] discard;

        // Create the tf-idf representation of the cluster labels
        for (int c = 0; c < candidateClusterCount; c++)
        {
            // Check if present in the phrase
            int [] featureIndices = features[phraseIndices[c]].getPhraseFeatureIndices();

            if (usePhrase[c])
            {
                for (int f = 0; f < featureIndices.length; f++)
                {
                    if (
                        (featureIndices[f] < termCount)
                            && !features[featureIndices[f]].isQueryWord()
                    ) // skip stopwords and query words
                    {
                        Q.set(featureIndices[f], c, Q.get(featureIndices[f], c) + 1);
                    }
                }

                // Multiply by the idf factor
                for (int f = 0; f < featureIndices.length; f++)
                {
                    if (featureIndices[f] < termCount)
                    {
                        Q.set(
                            featureIndices[f], c,
                            Q.get(featureIndices[f], c) * features[featureIndices[f]].getIdf()
                        );
                    }
                }
            }

            if (useSingleTerm[c])
            {
                Q.set(singleTermIndices[c], c, 1);
            }
        }

        MatrixUtils.normalizeColumnLengths(Q);

        // Eliminate overlapping clusters (in terms of description)
        discard = new boolean[candidateClusterCount];

        Matrix clusterClusterCos = Q.transpose().times(Q);

        for (int r = 0; r < clusterClusterCos.getRowDimension(); r++)
        {
            if (discard[r])
            {
                continue;
            }

            for (int c = 0; c < clusterClusterCos.getColumnDimension(); c++)
            {
                if (
                    (c != r)
                        && (Math.abs(clusterClusterCos.get(r, c)) > duplicateClustersThreshold)
                        && (candidateClusterScores[r] > candidateClusterScores[c])
                )
                {
                    discard[c] = true;
                }
            }
        }

        // Find candidate cluster scores 
        clusterCount = 0;

        for (int cc = 0; cc < candidateClusterScores.length; cc++)
        {
            if (!discard[cc])
            {
                clusterCount++;
            }
        }

        // Remove unnecessary columns from U and V matrices
        int [] columns = new int[clusterCount];
        int col = 0;

        for (int cc = 0; cc < discard.length; cc++)
        {
            if (!discard[cc])
            {
                columns[col++] = cc;
            }
        }

        U = U.getMatrix(0, U.getRowDimension() - 1, columns);
        Q = Q.getMatrix(0, Q.getRowDimension() - 1, columns);
        V = V.getMatrix(0, V.getRowDimension() - 1, columns);
        S = S.getMatrix(columns, columns);

        singleTermIndices = ArrayUtils.project(singleTermIndices, columns);
        phraseIndices = ArrayUtils.project(phraseIndices, columns);
        usePhrase = ArrayUtils.project(usePhrase, columns);
        useSingleTerm = ArrayUtils.project(useSingleTerm, columns);

        double [] clusterScores = ArrayUtils.project(candidateClusterScores, columns);

        // Create clusters
        candidateClusters = new Cluster[clusterCount + 1];

        for (int i = 0; i < candidateClusters.length; i++)
        {
            candidateClusters[i] = new Cluster();
        }

        // Add labels to clusters
        for (int c = 0; c < clusterCount; c++)
        {
            candidateClusters[c].setScore(clusterScores[c]);

            if (usePhrase[c])
            {
                candidateClusters[c].addLabel(features[phraseIndices[c]].getText());
            }

            if (useSingleTerm[c])
            {
                candidateClusters[c].addLabel(features[singleTermIndices[c]].getText());
            }
        }
    }


    /**
     * Assigns snippets to clusters based on cluster labels.
     */
    protected Cluster [] createClusters()
    {
        ArrayList finalClusters = new ArrayList();

//        Matrix approx = (U.times(S)).times(V.transpose());
        Matrix approx = tdMatrix;

//        MatrixUtils.normalizeColumnLengths(approx);
        Matrix snippetClusterCos = approx.transpose().times(Q);

        // Assign snippets to clusters
        boolean [] assigned = new boolean[snippets.length];
        double [] snippetMaxScores = new double[docCount];

        for (int j = 0; j < snippetClusterCos.getRowDimension(); j++)
        {
            // Overlapping
            for (int c = 0; c < clusterCount; c++)
            {
                if (Math.abs(snippetClusterCos.get(j, c)) >= clusterAssignmentThreshold)
                {
                    candidateClusters[c].addSnippet(
                        snippets[j], Math.abs(snippetClusterCos.get(j, c))
                    );
                    assigned[j] = true;
                }

                if (Math.abs(snippetClusterCos.get(j, c)) > snippetMaxScores[j])
                {
                    snippetMaxScores[j] = Math.abs(snippetClusterCos.get(j, c));
                }
            }
        }

        // Calculate scores
        double maxScore = 0;

        for (int c = 0; c < clusterCount; c++)
        {
            candidateClusters[c].setScore(
                candidateClusters[c].getScore() * candidateClusters[c].getSnippets().length
            );

            if (maxScore < candidateClusters[c].getScore())
            {
                maxScore = candidateClusters[c].getScore();
            }
        }

        double [] clusterScores = new double[clusterCount];

        for (int c = 0; c < clusterCount; c++)
        {
            candidateClusters[c].setScore(candidateClusters[c].getScore() / maxScore);
            clusterScores[c] = candidateClusters[c].getScore();
        }

        // Create "Other" group
        Cluster other = new Cluster();
        other.setOtherTopics(true);
        other.addLabel("(Other)");
        other.setScore(0.00001);

        for (int s = 0; s < snippets.length; s++)
        {
            if (!assigned[s])
            {
                other.addSnippet(snippets[s], snippetMaxScores[s]);
            }
        }

        // Create "Singletons" group
        Cluster singletons = new Cluster();
        singletons.setOtherTopics(true);
        singletons.addLabel("(Singletons)");
        singletons.setScore(0.00002);

        // Create final clusters
        for (int i = 0; i < candidateClusters.length; i++)
        {
            if (candidateClusters[i].getSnippets().length > 1)
            {
                finalClusters.add(candidateClusters[i]);
            }
            else if (candidateClusters[i].getSnippets().length == 1)
            {
                singletons.addCluster(candidateClusters[i]);
            }
        }

        if (singletons.getClusters().length > 0)
        {
            other.addCluster(singletons);
        }

        if (other.getSnippets().length > 0)
        {
            finalClusters.add(other);
        }

        // Sort
        Collections.sort(finalClusters);

        return (Cluster []) finalClusters.toArray(new Cluster[finalClusters.size()]);
    }
}
