/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import org.apache.commons.collections.primitives.*;

import cern.colt.list.*;
import cern.colt.map.*;
import cern.colt.matrix.*;
import cern.jet.math.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.lingo.model.*;
import com.stachoodev.matrix.*;
import com.stachoodev.matrix.factorization.*;
import com.stachoodev.util.common.*;

/**
 * Note: due to a dramatic performance loss, we avoid performing any linear
 * algebra operations on sparse matrices. The cost for this is much larger
 * memory footprint.
 * 
 * TODO: the sparse term-phrase matrix introduces a lot of performance loss. In
 * the production implementation, the label discover phrase should be
 * implemented without explicitly forming this matrix.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoWorker
{
    /** */
    private MatrixFactorizationFactory matrixFactorizationFactory;

    /** */
    private int qualityLevel;

    /** */
    private double minTf;

    /** */
    private double minEigenvalue = 0.05;

    /** Master selected terms */
    private List selectedTerms;

    /** Master frequent phrases */
    private List frequentPhrases;

    /** Master (sparse) term-document matrix */
    private DoubleMatrix2D masterTdMatrix;

    /** Master (sparse) phrase matrix */
    private DoubleMatrix2D masterPhraseMatrix;

    /** */
    private ModelBuilderContext modelBuilderContext;

    /**
     * Dense submatrix of the master term-document matrix with selected terms
     * and documents
     */
    private DoubleMatrix2D selectedTdMatrix;

    /** Submatrix of the master phrase matrix with selected terms. */
    private DoubleMatrix2D selectedPhraseMatrix;

    /**
     * Documents omitted in the term-document matrix due to all-zero columns.
     * Indices correspond to the global document list from the
     * ModelBuilderContext.
     */
    private IntArrayList omittedDocumentIndices;

    /**
     * Indices of currently selected terms. The indices refer to the global term
     * list from the ModelBuilderContext.
     */
    private IntArrayList selectedTermIndices;

    /**
     * Indices of currently selected documents. The indices refer to the global
     * document list from the ModelBuilderContext.
     */
    private IntArrayList selectedDocumentIndices;

    /** Cluster count estimated by eigenvalue analysis */
    private int estimatedClusterCount;

    /** */
    private MatrixFactorization matrixFactorization;

    /** Label vector matrix */
    private DoubleMatrix2D labelVectors;

    /** A list of LingoClusterLabel objects */
    private List clusterLabels;

    /** Execution profile */
    private Profile profile;

    /** */
    private int pass;

    /** */
    private String passInfo;

    /** */
    private int level;

    /**
     * @param selectedTerms
     * @param frequentPhrases
     * @param masterTdMatrix
     * @param masterPhraseMatrix
     * @param modelBuilderContext
     */
    public LingoWorker(List selectedTerms, List frequentPhrases,
        DoubleMatrix2D masterTdMatrix, DoubleMatrix2D masterPhraseMatrix,
        ModelBuilderContext modelBuilderContext, Profile profile)
    {
        this.selectedTerms = selectedTerms;
        this.frequentPhrases = frequentPhrases;
        this.masterTdMatrix = masterTdMatrix;
        this.masterPhraseMatrix = masterPhraseMatrix;
        this.modelBuilderContext = modelBuilderContext;
        this.profile = profile;

        matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
        ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
            .setMaxIterations(15);
        ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
            .setOrdered(true);
    }

    /**
     * @param sourceCluster
     * @param majorClusters
     * @param pass
     * @param level
     * @param qualityLevel
     * @return
     */
    public List createMinorClusters(LingoCluster sourceCluster,
        List majorClusters, int pass, int level, int qualityLevel)
    {
        this.pass = pass;
        this.level = level;

        // Pass info in profiling
        passInfo = sourceCluster.getLabel().toString() + " "
            + Integer.toString(pass);

        // Set parameters for the current pass
        setParameters();

        long totalStart = System.currentTimeMillis();

        long start = 0;
        long stop = 0;

        start = System.currentTimeMillis();
        boolean containsData = createSelectedMatrices(sourceCluster,
            majorClusters);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Matrix projection", passInfo,
            stop - start);

        if (!containsData)
        {
            return Collections.EMPTY_LIST;
        }

        start = System.currentTimeMillis();
        estimatedClusterCount = estimateClusterCount();
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Cluster count estimation",
            passInfo, stop - start);

        if (estimatedClusterCount == 0)
        {
            return Collections.EMPTY_LIST;
        }

        start = System.currentTimeMillis();
        factorise();
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Matrix factorisation",
            passInfo, stop - start);

        start = System.currentTimeMillis();
        discoverLabels();
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Label discovery", passInfo,
            stop - start);

        start = System.currentTimeMillis();
        List clusters = createClusters(sourceCluster);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Cluster creation", passInfo,
            stop - start);

        LingoProfilingUtils.logMeantime(profile, "Lingo Worker Total",
            passInfo, System.currentTimeMillis() - totalStart);

        return clusters;
    }

    /**
     */
    private void setParameters()
    {
        minTf = (5 - pass * 1 - level > 3 ? 5 - pass * 1 - level : 3);
//        minEigenvalue = 0.4 - level * 0.1;
        minEigenvalue = (0.05 - pass * 0.01 > 0.03 ? 0.05 - pass * 0.01 : 0.03);
//            - 0.01 * level;

        if (pass == 0)
        {
            matrixFactorizationFactory = new LocalNonnegativeMatrixFactorizationFactory();
        }
        else
        {
            matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
        }
        ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
            .setMaxIterations(15);
        ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
            .setOrdered(true);
    }

    /**
     * @return
     */
    private boolean createSelectedMatrices(LingoCluster sourceCluster,
        List majorClusters)
    {
        // Add to the list all terms that don't meet the minTf criterion
        IntArrayList allTerms = modelBuilderContext.getSelectedFeatureCodes();
        IntArrayList termsToOmit = extractTermsToOmit(sourceCluster,
            majorClusters);
        DoubleArrayList termFrequencies = modelBuilderContext
            .getTermFrequencies();
        for (int i = 0; i < termFrequencies.size(); i++)
        {
            if (termFrequencies.get(i) < minTf)
            {
                termsToOmit.add(allTerms.get(i));
            }
        }

        // Create a list of selected term indices
        selectedTermIndices = new IntArrayList((allTerms.size()
            - termsToOmit.size() > 0 ? allTerms.size() - termsToOmit.size()
            : allTerms.size()));
        if (termsToOmit.size() > 0)
        {
            termsToOmit.sort();
            int j = 0;
            for (int i = 0; i < allTerms.size(); i++)
            {
                // We may have duplicates in the termsToOmit list
                while (j < termsToOmit.size()
                    && allTerms.getQuick(i) > termsToOmit.getQuick(j))
                {
                    j++;
                }

                if (j >= termsToOmit.size()
                    || allTerms.getQuick(i) != termsToOmit.getQuick(j))
                {
                    selectedTermIndices.add(i);
                }
            }
            selectedTermIndices.trimToSize();
        }
        else
        {
            for (int i = 0; i < allTerms.size(); i++)
            {
                selectedTermIndices.add(i);
            }
        }

        // No terms? Don't bother going any further
        if (selectedTermIndices.size() == 0)
        {
            return false;
        }

        // Preliminary version of the selected term-document matrix
        DoubleMatrix2D sparseSelectedTdMatrix = masterTdMatrix.viewSelection(
            selectedTermIndices.elements(), null);

        // Check the matrix for all-zero columns
        selectedDocumentIndices = extractDocumentsToRetain(sourceCluster,
            majorClusters);
        omittedDocumentIndices = new IntArrayList();
        for (int c = 0; c < sparseSelectedTdMatrix.columns(); c++)
        {
            if (sparseSelectedTdMatrix.viewColumn(c).cardinality() == 0)
            {
                int index = selectedDocumentIndices.binarySearch(c);
                if (index >= 0)
                {
                    omittedDocumentIndices.add(c);
                    selectedDocumentIndices.remove(index);
                }
            }
        }
        selectedDocumentIndices.trimToSize();

        // No documents? Don't bother going any further
        if (selectedDocumentIndices.size() == 0)
        {
            return false;
        }

        // Final selection on the term-document matrix
        sparseSelectedTdMatrix = sparseSelectedTdMatrix.viewSelection(null,
            selectedDocumentIndices.elements());

        // Convert to a dense NNI matrix
        selectedTdMatrix = NNIDoubleFactory2D
            .asNNIMatrix(sparseSelectedTdMatrix);

        // Final selection on the phrase matrix
        // Note: in theory we could also remove some all-zero columns, but
        // the cost of doing so may be larger than the cost of matching
        // a few labels more
        selectedPhraseMatrix = NNIDoubleFactory2D
            .asNNIMatrix(masterPhraseMatrix.viewSelection(selectedTermIndices
                .elements(), null));

        // Note: in theory we should L2-normalise the columns of the selected
        // phrase matrix (removing rows may remove non-zero entries and thus
        // make the L2 of a column not equal to 1.0 anymore), but to save time
        // we don't do that. It doesn't seem to affect the results anyway.
        MatrixUtils.normaliseColumnL2(selectedPhraseMatrix, null);

        return true;
    }

    /**
     * @param sourceCluster
     * @param majorClusters
     * @return
     */
    private IntArrayList extractTermsToOmit(LingoCluster sourceCluster,
        List majorClusters)
    {
        IntArrayList termsToOmit = new IntArrayList();

        // Omit terms appearing in the source cluster's and its parents' label
        addClusterLabels(termsToOmit, sourceCluster);

        // Omit terms appearing in the major clusters' labels
        for (Iterator iter = majorClusters.iterator(); iter.hasNext();)
        {
            LingoCluster majorCluster = (LingoCluster) iter.next();
            if (!majorCluster.isJunkCluster())
            {
                int [] wordCodes = majorCluster.getLabel().getWordCodes();
                termsToOmit.addAllOf(new IntArrayList(wordCodes));
            }
        }

        return termsToOmit;
    }

    /**
     * @param termsToOmit
     * @param cluster
     */
    private void addClusterLabels(IntArrayList termsToOmit, LingoCluster cluster)
    {
        if (!cluster.isJunkCluster())
        {
            int [] wordCodes = cluster.getLabel().getWordCodes();
            if (wordCodes != null)
            {
                termsToOmit.addAllOf(new IntArrayList(wordCodes));
            }
        }

        if (cluster.getParent() != null)
        {
            addClusterLabels(termsToOmit, cluster.getParent());
        }
    }

    /**
     * Passing nul
     * 
     * @param sourceCluster
     * @param majorClusters
     * @return
     */
    private IntArrayList extractDocumentsToRetain(LingoCluster sourceCluster,
        List majorClusters)
    {
        IntArrayList documentsToRetain = (IntArrayList) sourceCluster
            .getDocumentIndices().clone();

        documentsToRetain.sort();

        //        for (Iterator iter = majorClusters.iterator(); iter.hasNext();)
        //        {
        //            LingoCluster lingoCluster = (LingoCluster) iter.next();
        //
        //            if (!lingoCluster.isJunkCluster())
        //            {
        //                IntArrayList documents = lingoCluster.getDocumentIndices();
        //
        //                for (int i = 0; i < documents.size(); i++)
        //                {
        //                    int index = documentsToRetain.binarySearch(documents
        //                        .get(i));
        //                    if (index >= 0)
        //                    {
        //                        documentsToRetain.remove(index);
        //                    }
        //                }
        //            }
        //        }

        return documentsToRetain;
    }

    /**
     * @return
     */
    private int estimateClusterCount()
    {
        //             T
        // Create the A A matrix
        DoubleMatrix2D ATA = selectedTdMatrix.zMult(selectedTdMatrix, null, 1,
            0, true, false);

        // Compute its eigenvalues
        double [] eigenvalues = EigenvalueCalculator
            .computeEigenvaluesSymmetrical(ATA);

        // Sort and normalise
        java.util.Arrays.sort(eigenvalues);
        double max = eigenvalues[eigenvalues.length - 1];
        int i = 0;
        for (i = 0; i < eigenvalues.length; i++)
        {
            eigenvalues[i] /= max;
        }

        // Find cluster count
        double diff = 0;
        for (i = (int) (eigenvalues.length * 0.5); i < eigenvalues.length; i++)
        {
            if (eigenvalues[i] == 0)
            {
                continue;
            }
            diff = (eigenvalues[i] - eigenvalues[i - 1]);
//            diff = eigenvalues[i];

            if (diff > minEigenvalue)
            {
                break;
            }
        }

        int clusterCount = (eigenvalues.length - i);

        LingoProfilingUtils.logInfo(profile, "Eigenvalues",
            passInfo, cern.colt.Arrays.toString(eigenvalues));
        LingoProfilingUtils.logInfo(profile, "Estimated Cluster Count",
            passInfo, new Integer(clusterCount));

        if (clusterCount < 2)
        {
            return 0;
        }

        if (clusterCount > 25)
        {
            clusterCount = 25;
        }

        return clusterCount;
    }

    /**
     * @return
     */
    private void factorise()
    {
        // Set the number of base vectors
        if (matrixFactorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
                .setK(estimatedClusterCount);
        }
        else if (matrixFactorizationFactory instanceof PartialSingularValueDecompositionFactory)
        {
            ((PartialSingularValueDecompositionFactory) matrixFactorizationFactory)
                .setK(estimatedClusterCount);
        }

        // Set the estimated number of iterations
        if (matrixFactorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            IterationNumberGuesser
                .setEstimatedIterationsNumber(
                    (IterativeMatrixFactorizationFactory) matrixFactorizationFactory,
                    selectedTdMatrix, qualityLevel);
        }

        // Factorize the A matrix
        matrixFactorization = matrixFactorizationFactory
            .factorize(selectedTdMatrix);
    }

    /**
     * @return
     */
    private List discoverLabels()
    {
        // Normalise base vectors
        DoubleMatrix2D U = matrixFactorization.getU();
        MatrixUtils.normaliseColumnL2(U, null);

        double [] aggregates = null;
        if (matrixFactorization instanceof IterativeMatrixFactorizationBase)
        {
            aggregates = ((IterativeMatrixFactorizationBase) matrixFactorization)
                .getAggregates();
        }
        else if (matrixFactorization instanceof PartialSingularValueDecomposition)
        {
            aggregates = ((PartialSingularValueDecomposition) matrixFactorization)
                .getSingularValues();
        }

        // For each base vector determine which phrase or single term describes
        // it best. Cosine similarity will be used.
        LingoClusterLabel [] candidateLabels = new LingoClusterLabel [U
            .columns()];

        // Cosines between base vectors and phrase vectors
        DoubleMatrix2D phraseCos = selectedPhraseMatrix.zMult(U, null, 1, 0,
            true, false);

        // Apply corrective scaling for single terms
        phraseCos.viewPart(0, 0, phraseCos.rows() - frequentPhrases.size(),
            phraseCos.columns()).assign(Functions.div(1.45));

        // Find maximum phrase TF
        double maxPhraseFrequency = ((ExtendedTokenSequence) Collections.max(
            frequentPhrases, PropertyHelper.getComparatorForDoubleProperty(
                ExtendedTokenSequence.PROPERTY_TF, false))).getDoubleProperty(
            ExtendedTokenSequence.PROPERTY_TF, 1);

        // Apply corrective scaling for phrases
        for (int r = phraseCos.rows() - frequentPhrases.size(); r < phraseCos
            .rows(); r++)
        {
            ExtendedTokenSequence phrase = (ExtendedTokenSequence) frequentPhrases
                .get(r - (phraseCos.rows() - frequentPhrases.size()));
            DoubleMatrix1D row = phraseCos.viewRow(r);

            // Penalize overlong phrases
            if (phrase.getLength() >= 8)
            {
                row.assign(0);
            }
            else if (phrase.getLength() > 5)
            {
                row.assign(Functions.mult(1 - (1 / 5.0)
                    * (phrase.getLength() - 5)));
            }

            // Adjust most frequent phrases
            double freq = phrase.getDoubleProperty(
                ExtendedTokenSequence.PROPERTY_TF, maxPhraseFrequency);
            row.assign(Functions.mult(Math.pow(Math.log(freq)
                / Math.log(maxPhraseFrequency), 0.25)));
        }

        // Select best fits
        int [] phraseIndices = MatrixUtils.maxInColumns(phraseCos, null, null);

        // Create candidate labels and vectors
        for (int i = 0; i < phraseIndices.length; i++)
        {
            if (phraseIndices[i] < phraseCos.rows() - frequentPhrases.size())
            {
                // Single term
                candidateLabels[i] = new LingoClusterLabel(selectedTerms.get(
                    phraseIndices[i]).toString(), new int []
                { modelBuilderContext.getSelectedFeatureCodes().get(
                    phraseIndices[i]) });
            }
            else
            {
                // Phrase
                candidateLabels[i] = new LingoClusterLabel(frequentPhrases.get(
                    phraseIndices[i]
                        - (phraseCos.rows() - frequentPhrases.size()))
                    .toString(), (int []) modelBuilderContext
                    .getExtractedPhraseCodes().get(
                        phraseIndices[i]
                            - (phraseCos.rows() - frequentPhrases.size())));
            }

            double clusterScore = phraseCos.getQuick(phraseIndices[i], i);
            if (aggregates != null)
            {
                clusterScore *= aggregates[i];
            }
            candidateLabels[i].setScore(clusterScore);
        }
        labelVectors = selectedPhraseMatrix.viewSelection(null, phraseIndices);

        // Cosines between candidate labels and columns of U
        // Need them in an array to sort the cos matrix
        double [] cosines = new double [U.columns()];

        // Minus sign to order decreasingly
        for (int i = 0; i < cosines.length; i++)
        {
            cosines[i] = -candidateLabels[i].getScore();
        }

        // Sort labels in order to remove duplicates
        labelVectors = cern.colt.matrix.doublealgo.Sorting.mergeSort.sort(labelVectors.viewDice(), cosines);
        java.util.Arrays.sort(candidateLabels);

        // Get rid of duplicates
        DoubleMatrix2D coscos = labelVectors.zMult(labelVectors, null, 1.0, 0,
            false, true);
        for (int i = 1; i < coscos.columns(); i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (candidateLabels[i] != null && coscos.getQuick(j, i) > 0.3)
                {
                    candidateLabels[i] = null;
                }
            }
        }

        clusterLabels = new ArrayList();
        IntList columns = new ArrayIntList();
        for (int i = 0; i < candidateLabels.length; i++)
        {
            if (candidateLabels[i] != null)
            {
                clusterLabels.add(candidateLabels[i]);
                columns.add(i);
            }
        }

        // Remove columns from the label vectors matrix
        labelVectors = labelVectors.viewDice().viewSelection(null,
            columns.toArray());
        DoubleMatrix2D newLabelVectors = NNIDoubleFactory2D.nni.make(
            labelVectors.rows(), labelVectors.columns());
        newLabelVectors.assign(labelVectors);
        labelVectors = newLabelVectors;

        LingoProfilingUtils.logInfo(profile, "Actual Cluster Count", passInfo,
            new Integer(clusterLabels.size()));

        return clusterLabels;
    }

    /**
     *  
     */
    private List createClusters(LingoCluster parentCluster)
    {
        // Standard vector space model assignment
        MatrixUtils.normaliseColumnL2(selectedTdMatrix, null);
        DoubleMatrix2D cos = selectedTdMatrix.zMult(labelVectors, null, 1.0, 0,
            true, false);

        OpenIntIntHashMap assignedDocuments = new OpenIntIntHashMap(
            selectedDocumentIndices.size());
        List clusters = new ArrayList();
        double maxScore = -1;
        for (int c = 0; c < cos.columns(); c++)
        {
            LingoCluster cluster = new LingoCluster(parentCluster,
                (LingoClusterLabel) clusterLabels.get(c), false);

            // Add documents
            for (int r = 0; r < cos.rows(); r++)
            {
                if (cos.getQuick(r, c) >= 0.20)
                {
                    cluster.addMember(new LingoClusterMember(
                        selectedDocumentIndices.get(r), cos.getQuick(r, c)));
                    assignedDocuments.put(selectedDocumentIndices.get(r),
                        selectedDocumentIndices.get(r));
                }
            }

            // Don't bother if no documents
            if (cluster.getMembers().size() == 0)
            {
                continue;
            }

            // Order documents within the cluster by document score
            cluster.sortMembers();

            // Set score for the cluster (will be normalized later)
            double score = cluster.getMembers().size()
                * cluster.getLabel().getScore();
            cluster.setScore(score / (pass * 2 + 1));
            if (maxScore < score)
            {
                maxScore = score;
            }

            clusters.add(cluster);
        }

        // Normalize scores
        for (Iterator iter = clusters.iterator(); iter.hasNext();)
        {
            LingoCluster cluster = (LingoCluster) iter.next();
            cluster.setScore(cluster.getScore() / maxScore);
        }

        // Sort clusters by their score
        Collections.sort(clusters);

        // Remove clusters with only one document
        for (int i = clusters.size() - 1; i >= 0; i--)
        {
            LingoCluster cluster = (LingoCluster) clusters.get(i);
            if (cluster.getMembers().size() == 1)
            {
                clusters.remove(i);
            }
        }

        // Create the other topics group
        LingoCluster otherTopics = new LingoCluster(parentCluster,
            new LingoClusterLabel("(Other Topics)", null), true);
        otherTopics.setScore(0.0001);
        for (int d = 0; d < selectedDocumentIndices.size(); d++)
        {
            if (!assignedDocuments.containsKey(selectedDocumentIndices.get(d)))
            {

                double score = 0;
                score = cos.get(d, MatrixUtils.maxInRow(cos, d));
                otherTopics.addMember(new LingoClusterMember(
                    selectedDocumentIndices.get(d), score));
            }
        }
        for (int i = 0; i < omittedDocumentIndices.size(); i++)
        {
            otherTopics.addMember(new LingoClusterMember(omittedDocumentIndices
                .get(i), 0));
        }

        if (otherTopics.getMembers().size() > 0)
        {
            clusters.add(otherTopics);
        }

        return clusters;
    }
}