/*
 * Lingo.java Created on 2004-06-17
 */
package com.stachoodev.carrot.filter.lingo.algorithm;

import java.util.*;

import org.apache.commons.collections.primitives.*;
import cern.colt.matrix.*;
import cern.colt.matrix.doublealgo.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.carrot.filter.lingo.model.*;
import com.stachoodev.matrix.*;
import com.stachoodev.matrix.factorization.*;
import com.stachoodev.util.common.*;

/**
 * @author stachoo
 */
public class Lingo
{
    /**
     * Returns an instance of {@link Double}being the score of this document as
     * a member of a cluster.
     */
    public static final String PROPERTY_CLUSTER_MEMBER_SCORE = "mscore";

    /**
     * Determines the feature selection strategy to be used by this instance of
     * Lingo. The value of this parameter must implement
     * {@link FeatureSelectionStrategy}.
     */
    public static final String PARAMETER_FEATURE_SELECTION_STRATEGY = "feature-selection";

    /**
     * Determines the phrase extraction strategy to be used by this instance of
     * Lingo. The value of this parameter must implement
     * {@link PhraseExtractionStrategy}.
     */
    public static final String PARAMETER_PHRASE_EXTRACTION_STRATEGY = "phrase-extraction";

    /**
     * Determines the term-document matrix building strategy to be used by this
     * instance of Lingo. The value of this parameter must implement
     * {@link TdMatrixBuildingStrategy}.
     */
    public static final String PARAMETER_TD_MATRIX_BUILDING_STRATEGY = "tdmatrix-building";

    /**
     * Determines matrix factorization strategy to be used by this instance of
     * Lingo. The value of this parameter must implement
     * {@link MatrixFactorizationFactory}.
     */
    public static final String PARAMETER_MATRIX_FACTORIZATION_FACTORY = "matrix-factorization";

    /** A list of tokenized documents */
    private List tokenizedDocuments;

    /** An algorithm for feature selection */
    private FeatureSelectionStrategy featureSelectionStrategy;

    /** Phrase extraction strategy to be used */
    private PhraseExtractionStrategy phraseExtractionStrategy;

    /** An algorithm for creating the term-document matrix */
    private TdMatrixBuildingStrategy tdMatrixBuildingStrategy;

    /** Matrix factorization to be used for cluster discovery */
    private MatrixFactorizationFactory matrixFactorizationFactory;

    /** Selected single terms */
    private List selectedTerms;

    /** Selected single terms in a map */
    private Map selectedTermsMap;

    /** Frequent phrases */
    private List frequentPhrases;

    /** Term-document matrix */
    private DoubleMatrix2D tdMatrix;

    /** Factorization of the term-document matrix */
    private MatrixFactorization matrixFactorization;

    /** Label score property */
    private static final String PROPERTY_LABEL_SCORE = "lscore";

    /** Cluster label vectors */
    private DoubleMatrix2D labelVectors;

    /** Cluster labels */
    private List clusterLabels;

    /**
     * Creates an instance of Lingo with default parameters.
     */
    public Lingo()
    {
        this(new HashMap());
    }

    /**
     * Creates an instance of Lingo with given parameters. See descriptions of
     * constants for possible parameter names and values.
     * 
     * @param parameters
     */
    public Lingo(Map parameters)
    {
        // Set feature selection
        if (parameters.get(PARAMETER_FEATURE_SELECTION_STRATEGY) != null)
        {
            this.featureSelectionStrategy = (FeatureSelectionStrategy) parameters
                .get(PARAMETER_FEATURE_SELECTION_STRATEGY);
        }
        else
        {
            this.featureSelectionStrategy = new TfFeatureSelectionStrategy();
        }

        // Set phrase extraction
        if (parameters.get(PARAMETER_PHRASE_EXTRACTION_STRATEGY) != null)
        {
            this.phraseExtractionStrategy = (PhraseExtractionStrategy) parameters
                .get(PARAMETER_PHRASE_EXTRACTION_STRATEGY);
        }
        else
        {
            this.phraseExtractionStrategy = new SAPhraseExtractionStrategy();
        }

        // Set td matrix building
        if (parameters.get(PARAMETER_TD_MATRIX_BUILDING_STRATEGY) != null)
        {
            this.tdMatrixBuildingStrategy = (TdMatrixBuildingStrategy) parameters
                .get(PARAMETER_TD_MATRIX_BUILDING_STRATEGY);
        }
        else
        {
            this.tdMatrixBuildingStrategy = new TfIdfTdMatrixBuildingStrategy();
        }

        // Set matrix factorization
        if (parameters.get(PARAMETER_MATRIX_FACTORIZATION_FACTORY) != null)
        {
            this.matrixFactorizationFactory = (MatrixFactorizationFactory) parameters
                .get(PARAMETER_MATRIX_FACTORIZATION_FACTORY);
        }
        else
        {
            this.matrixFactorizationFactory = new NonnegativeMatrixFactorizationEDFactory();
            ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
                .setMaxIterations(15);
            ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
                .setOrdered(true);
        }

    }

    /**
     * @param rawDocuments
     * @return
     */
    public List cluster(List tokenizedDocuments)
    {
        this.tokenizedDocuments = tokenizedDocuments;

        if (prepare())
        {
            clusterLabels = discoverLabels();
            return createClusters();
        }
        else
        {
            return new ArrayList();
        }
    }

    /**
     *  
     */
    private boolean prepare()
    {
        selectedTerms = featureSelectionStrategy
            .getSelectedFeatures(tokenizedDocuments);

        if (selectedTerms.size() == 0)
        {
            // No frequent terms - don't bother to go any further
            return false;
        }

        selectedTermsMap = ModelUtils.tokenListAsMap(selectedTerms);

        tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(tokenizedDocuments,
            selectedTerms);
        MatrixUtils.normaliseColumnL2(tdMatrix, null);

        frequentPhrases = phraseExtractionStrategy
            .getExtractedPhrases(tokenizedDocuments);

        return true;
    }

    /**
     * @return
     */
    private List discoverLabels()
    {
        // Factorize the A matrix
        matrixFactorization = matrixFactorizationFactory.factorize(tdMatrix);
        DoubleMatrix2D U = matrixFactorization.getU();
        MatrixUtils.normaliseColumnL2(U, null);

        // For each base vector determine which phrase or single term describes
        // it best. Cosine similarity will be used. To save memory, no phrase
        // matrix will be created
        ExtendedTokenSequence [] candidateLabels = new ExtendedTokenSequence [U
            .columns()];
        labelVectors = NNIDoubleFactory2D.nni.make(U.rows(), U.columns());

        // Cosines between current labels and columns of U
        double [] cosines = new double [U.columns()];

        // Check single words
        int [] singleWords = MatrixUtils.maxInColumn(U);
        for (int c = 0; c < U.columns(); c++)
        {
            candidateLabels[c] = ((ExtendedToken) selectedTerms
                .get(singleWords[c])).asTokenSequence();
            cosines[c] = U.getQuick(singleWords[c], c) / 1.2;
            candidateLabels[c].setDoubleProperty(PROPERTY_LABEL_SCORE,
                cosines[c]);

            // Update label vector
            labelVectors.setQuick(singleWords[c], c, 1.0);
        }

        // Check phrases
        double [] phraseVector = new double [U.rows()];
        for (int c = 0; c < U.columns(); c++)
        {
            for (Iterator phrases = frequentPhrases.iterator(); phrases
                .hasNext();)
            {
                // Calculate the cosine
                ExtendedTokenSequence phrase = (ExtendedTokenSequence) phrases
                    .next();
                double cosine = 0;
                double length = 0;
                Arrays.fill(phraseVector, 0);
                for (int t = 0; t < phrase.getLength(); t++)
                {
                    Token token = phrase.getTokenAt(t);
                    if (selectedTermsMap.containsKey(token.toString()))
                    {
                        ExtendedToken extendedToken = (ExtendedToken) selectedTermsMap
                            .get(token.toString());

                        // Weight of a single token in the phrase
                        double weight = extendedToken.getDoubleProperty(
                            ExtendedToken.PROPERTY_TF, 0);
                        weight *= extendedToken.getDoubleProperty(
                            ExtendedToken.PROPERTY_IDF, 1);
                        length += weight * weight;
                        phraseVector[extendedToken.getIntProperty(
                            ExtendedToken.PROPERTY_INDEX, -1)] = weight;

                        // Cosine between a column of U an the phrase vector
                        cosine += U.getQuick(extendedToken.getIntProperty(
                            ExtendedToken.PROPERTY_INDEX, -1), c)
                            * weight;
                    }
                }

                length = Math.sqrt(length);
                cosine /= length;
                for (int i = 0; i < phraseVector.length; i++)
                {
                    phraseVector[i] /= length;
                }

                // Compare
                if (cosine >= cosines[c])
                {
                    if (phrase.getDoubleProperty(PROPERTY_LABEL_SCORE, -2) < cosine)
                    {
                        phrase.setDoubleProperty(PROPERTY_LABEL_SCORE,
                            cosine);
                    }
                    cosines[c] = cosine;
                    candidateLabels[c] = phrase;

                    // Update label vector
                    labelVectors.viewColumn(c).assign(phraseVector);
                }
            }
        }

        // Need to update the cosines array, as it may happen that some token
        // sequences will appear in the array more than once, which would
        // result in incosistence between the LABEL_SCORE property and the
        // cosine array. And this would screw up sorting...
        // Minus sign to order decreasingly
        for (int i = 0; i < cosines.length; i++)
        {
            cosines[i] = -candidateLabels[i]
                .getDoubleProperty(PROPERTY_LABEL_SCORE, 0);
        }

        labelVectors = Sorting.mergeSort.sort(labelVectors.viewDice(), cosines);
        Arrays.sort(candidateLabels, PropertyHelper
            .getComparatorForDoubleProperty(PROPERTY_LABEL_SCORE, true));

        // Get rid of duplicates
        DoubleMatrix2D cos = labelVectors.zMult(labelVectors, null, 1.0, 0,
            false, true);
        for (int i = 1; i < cos.columns(); i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (candidateLabels[i] != null && cos.getQuick(j, i) > 0.3)
                {
                    candidateLabels[i] = null;
                }
            }
        }

        // Add the remaining ones to the list
        List clusterLabels = new ArrayList();
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

        return clusterLabels;
    }

    /**
     * @param count
     */
    public void setCandidateLabelsCount(int count)
    {
        if (matrixFactorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            ((IterativeMatrixFactorizationFactory) matrixFactorizationFactory)
                .setK(count);
        }
    }

    /**
     *  
     */
    private List createClusters()
    {
        // Standard vector space model assignment
        DoubleMatrix2D cos = tdMatrix.zMult(labelVectors, null, 1.0, 0, true,
            false);

        Set assignedDocuments = new HashSet();
        List clusters = new ArrayList();
        double maxScore = -1;
        for (int c = 0; c < cos.columns(); c++)
        {
            LingoRawCluster cluster = new LingoRawCluster();

            // Add label
            cluster
                .addLabel(((List) ((ExtendedTokenSequence) clusterLabels.get(c))
                    .getProperty(ExtendedTokenSequence.PROPERTY_ORIGINAL_TOKEN_SEQUENCES))
                    .get(0).toString());

            // Add documents
            for (int r = 0; r < cos.rows(); r++)
            {
                if (cos.getQuick(r, c) > 0.225)
                {
                    RawDocument rawDocument = (RawDocument) ((TokenizedDocument) tokenizedDocuments
                        .get(r))
                        .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT);

                    // If the rawDocument has already some member score it means
                    // it has already been assigned to some cluster. To preserve
                    // different member scores we need to clone the raw document
                    if (rawDocument.getProperty(PROPERTY_CLUSTER_MEMBER_SCORE) != null
                        && (rawDocument instanceof RawDocumentSnippet))
                    {
                        try
                        {
                            rawDocument = (RawDocument) ((RawDocumentSnippet) rawDocument)
                                .clone();
                        }
                        catch (CloneNotSupportedException ignored)
                        {
                        }
                    }

                    cluster.addDocument(rawDocument);
                    rawDocument.setProperty(PROPERTY_CLUSTER_MEMBER_SCORE,
                        new Double(cos.getQuick(r, c)));
                    assignedDocuments.add(tokenizedDocuments.get(r));
                }
            }

            // Don't bother if no documents
            if (cluster.getDocuments().size() == 0)
            {
                continue;
            }

            // Order documents within the cluster by document score
            Collections.sort(cluster.getDocuments(), PropertyHelper
                .getComparatorForDoubleProperty(PROPERTY_CLUSTER_MEMBER_SCORE,
                    true));

            // Set score for the cluster (will be normalized later)
            double score = cluster.getDocuments().size()
                * ((ExtendedTokenSequence) clusterLabels.get(c))
                    .getDoubleProperty(PROPERTY_LABEL_SCORE, 1);
            cluster.setScore(score);
            if (maxScore < score)
            {
                maxScore = score;
            }

            clusters.add(cluster);
        }

        // Normalize scores
        for (Iterator iter = clusters.iterator(); iter.hasNext();)
        {
            LingoRawCluster cluster = (LingoRawCluster) iter.next();
            cluster.setScore(cluster.getScore() / maxScore);
        }

        // Sort clusters by their score
        Collections.sort(clusters, PropertyHelper
            .getComparatorForDoubleProperty(LingoRawCluster.PROPERTY_SCORE,
                true));

        // Move all singletons to a single group
        LingoRawCluster singletons = new LingoRawCluster();
        singletons.addLabel("(Singletons)");
        singletons.setScore(0.002);
        for (int i = 0; i < clusters.size();)
        {
            LingoRawCluster cluster = (LingoRawCluster) clusters.get(i);
            if (cluster.getDocuments().size() == 1)
            {
                singletons.addDocument((RawDocument) cluster.getDocuments()
                    .get(0));
                clusters.remove(i);
            }
            else
            {
                i++;
            }
        }
        if (singletons.getDocuments().size() > 0)
        {
            clusters.add(singletons);
        }

        // Create the other topics group
        LingoRawCluster otherTopics = new LingoRawCluster();
        otherTopics.addLabel("(Other Topics)");
        otherTopics.setScore(0.002);
        for (Iterator iter = tokenizedDocuments.iterator(); iter.hasNext();)
        {
            TokenizedDocument tokenizedDocument = (TokenizedDocument) iter
                .next();

            if (!assignedDocuments.contains(tokenizedDocument))
            {
                otherTopics.addDocument((RawDocument) tokenizedDocument
                    .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT));
            }
        }
        if (otherTopics.getDocuments().size() > 0)
        {
            clusters.add(otherTopics);
        }

        return clusters;
    }
}