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

import cern.colt.matrix.*;
import cern.colt.matrix.doublealgo.*;
import cern.colt.matrix.linalg.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.lingo.model.*;
import com.stachoodev.matrix.*;
import com.stachoodev.matrix.factorization.*;
import com.stachoodev.util.common.*;

/**
 * Implementation of the Lingo algorithm.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
     * {@link FeatureSelectionStrategy}. The default is
     * {@link TfFeatureSelectionStrategy}.
     */
    public static final String PARAMETER_FEATURE_SELECTION_STRATEGY = "feature-selection";

    /**
     * Determines the phrase extraction strategy to be used by this instance of
     * Lingo. The value of this parameter must implement
     * {@link PhraseExtractionStrategy}. The default is
     * {@link SAPhraseExtractionStrategy}.
     */
    public static final String PARAMETER_PHRASE_EXTRACTION_STRATEGY = "phrase-extraction";

    /**
     * Determines the term-document matrix building strategy to be used by this
     * instance of Lingo. The value of this parameter must implement
     * {@link TdMatrixBuildingStrategy}. The default is
     * {@link TfTdMatrixBuildingStrategy}.
     */
    public static final String PARAMETER_TD_MATRIX_BUILDING_STRATEGY = "tdmatrix-building";

    /**
     * Determines matrix factorization strategy to be used by this instance of
     * Lingo. The value of this parameter must implement
     * {@link MatrixFactorizationFactory}. The default factory is
     * {@link NonnegativeMatrixFactorizationEDFactory}with 15 iterations and
     * random seeding.
     */
    public static final String PARAMETER_MATRIX_FACTORIZATION_FACTORY = "matrix-factorization";

    /**
     * Influences the balance between clustering quality and execution time.
     * Value of this parameter must be an Integer equal to 1 (low), 2 (medium)
     * or 3 (high quality, hopefully :). The default value is 2 (medium).
     */
    public static final String PARAMETER_QUALITY_LEVEL = "quality-level";
    private static final int DEFAULT_QUALITY_LEVEL = 2;

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

    /** Execution profile */
    private Profile profile;

    /** Balances between clustering quality an execution time */
    private int qualityLevel;

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
        // Set clustering quality
        if (parameters.get(PARAMETER_QUALITY_LEVEL) != null)
        {
            this.qualityLevel = ((Integer) parameters
                .get(PARAMETER_QUALITY_LEVEL)).intValue();

            if (qualityLevel < 1)
            {
                qualityLevel = 1;
            }

            if (qualityLevel > 3)
            {
                qualityLevel = 3;
            }
        }
        else
        {
            this.qualityLevel = DEFAULT_QUALITY_LEVEL;
        }

        // Set feature selection
        if (parameters.get(PARAMETER_FEATURE_SELECTION_STRATEGY) != null)
        {
            this.featureSelectionStrategy = (FeatureSelectionStrategy) parameters
                .get(PARAMETER_FEATURE_SELECTION_STRATEGY);
        }
        else
        {
            this.featureSelectionStrategy = new TfFeatureSelectionStrategy(
                5 - qualityLevel);
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
        long start = 0;
        long stop = 0;

        this.tokenizedDocuments = tokenizedDocuments;

        if (prepare())
        {
            start = System.currentTimeMillis();
            clusterLabels = discoverLabels();
            stop = System.currentTimeMillis();
            logMeantime("Label Discovery", stop - start);

            start = System.currentTimeMillis();
            List clusters = createClusters();
            stop = System.currentTimeMillis();
            logMeantime("Cluster Assignment", stop - start);

            // Log some info
            logInfo();

            return clusters;
        }
        else
        {
            return new ArrayList();
        }
    }

    /**
     *  
     */
    public void clear()
    {
        if (phraseExtractionStrategy instanceof SAPhraseExtractionStrategy)
        {
            ((SAPhraseExtractionStrategy) phraseExtractionStrategy).clear();
        }

        tokenizedDocuments = null;
        selectedTerms = null;
        selectedTermsMap = null;
        frequentPhrases = null;

        matrixFactorization = null;
        labelVectors = null;
        tdMatrix = null;
        profile = null;
    }

    /**
     *  
     */
    private void logInfo()
    {
        logInfo("Selected Terms", Integer.toString(selectedTerms.size()));
        logInfo("Extracted Phrases", Integer.toString(frequentPhrases.size()));
        logInfo("Term Document Matrix Dimensions", tdMatrix.rows() + " x "
            + tdMatrix.columns());
        logInfo("Term Document Matrix Sparseness", new Double(MatrixUtils
            .computeSparseness(tdMatrix)));
        logInfo("Term Document Matrix F-Norm", new Double(Algebra.DEFAULT
            .normF(tdMatrix)));

        if (profile != null)
        {
            profile.addProfileEntry("selected-terms", new ProfileEntry(
                "Selected Terms", null, selectedTerms));
            profile.addProfileEntry("extracted-phrases", new ProfileEntry(
                "Extracted Phrases", null, frequentPhrases));
        }
    }

    /**
     *  
     */
    private boolean prepare()
    {
        long start = 0;
        long stop = 0;

        start = System.currentTimeMillis();
        selectedTerms = featureSelectionStrategy
            .getSelectedFeatures(tokenizedDocuments);
        stop = System.currentTimeMillis();
        logMeantime("Feature Selection", stop - start);

        if (selectedTerms.size() == 0)
        {
            // No frequent terms - don't bother to go any further
            return false;
        }

        selectedTermsMap = ModelUtils.tokenListAsMap(selectedTerms);

        start = System.currentTimeMillis();
        tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(tokenizedDocuments,
            selectedTerms);
        MatrixUtils.normaliseColumnL2(tdMatrix, null);
        stop = System.currentTimeMillis();
        logMeantime("Term-Document Matrix", stop - start);

        start = System.currentTimeMillis();
        frequentPhrases = phraseExtractionStrategy
            .getExtractedPhrases(tokenizedDocuments);
        stop = System.currentTimeMillis();
        logMeantime("Phrase Extraction", stop - start);

        return true;
    }

    /**
     * @return
     */
    private List discoverLabels()
    {
        // Set the estimated number of iterations
        if (matrixFactorizationFactory instanceof IterativeMatrixFactorizationFactory)
        {
            IterationNumberGuesser
                .setEstimatedIterationsNumber(
                    (IterativeMatrixFactorizationFactory) matrixFactorizationFactory,
                    tdMatrix, qualityLevel);
        }

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
        int [] singleWords = MatrixUtils.maxInColumns(U, null, null);
        for (int c = 0; c < U.columns(); c++)
        {
            candidateLabels[c] = ((ExtendedToken) selectedTerms
                .get(singleWords[c])).asTokenSequence();
            cosines[c] = U.getQuick(singleWords[c], c) / 1.5;
            candidateLabels[c].setDoubleProperty(PROPERTY_LABEL_SCORE,
                cosines[c]);

            // Update label vector
            labelVectors.setQuick(singleWords[c], c, 1.0);
        }

        double maxPhraseFrequency = ((ExtendedTokenSequence) frequentPhrases
            .get(0)).getDoubleProperty(ExtendedTokenSequence.PROPERTY_TF, 1);

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
                        int index = extendedToken.getIntProperty(
                            ExtendedToken.PROPERTY_INDEX, -1);

                        // Weight of a single token in the phrase
                        double weight = extendedToken.getDoubleProperty(
                            ExtendedToken.PROPERTY_TF, 0);
                        weight *= extendedToken.getDoubleProperty(
                            ExtendedToken.PROPERTY_IDF, 1);
                        length += weight * weight;
                        phraseVector[index] = weight;

                        // Cosine between a column of U an the phrase vector
                        cosine += U.getQuick(index, c) * weight;
                    }
                }

                length = Math.sqrt(length);
                cosine /= length;
                for (int i = 0; i < phraseVector.length; i++)
                {
                    phraseVector[i] /= length;
                }

                // Penalize overlong phrases
                if (phrase.getLength() >= 8)
                {
                    cosine = 0;
                }
                else if (phrase.getLength() > 4)
                {
                    cosine *= 1 - (1 / 4.0) * (phrase.getLength() - 4);
                }

                double freq = phrase.getDoubleProperty(
                    ExtendedTokenSequence.PROPERTY_TF, maxPhraseFrequency);
                cosine *= Math.pow(Math.log(freq)
                    / Math.log(maxPhraseFrequency), 0.3);

                // Compare
                if (cosine >= cosines[c])
                {
                    if (phrase.getDoubleProperty(PROPERTY_LABEL_SCORE, -2) < cosine)
                    {
                        phrase.setDoubleProperty(PROPERTY_LABEL_SCORE, cosine);
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
            cosines[i] = -candidateLabels[i].getDoubleProperty(
                PROPERTY_LABEL_SCORE, 0);
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
            RawClusterBase cluster = new RawClusterBase();

            // Add label
            cluster
                .addLabel(((List) ((ExtendedTokenSequence) clusterLabels.get(c))
                    .getProperty(ExtendedTokenSequence.PROPERTY_ORIGINAL_TOKEN_SEQUENCES))
                    .get(0).toString());

            // Add documents
            int d = 0;
            for (int r = 0; r < cos.rows(); r++)
            {
                TokenizedDocument tokenizedDocument;
                do
                {
                    tokenizedDocument = (TokenizedDocument) tokenizedDocuments
                        .get(d);
                    d++;
                }
                while (tokenizedDocument
                    .getProperty(TdMatrixBuildingStrategy.PROPERTY_DOCUMENT_OMITTED) != null);

                if (cos.getQuick(r, c) >= 0.20)
                {
                    RawDocument rawDocument = (RawDocument) (tokenizedDocument)
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
                    assignedDocuments.add(tokenizedDocument);
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
            RawClusterBase cluster = (RawClusterBase) iter.next();
            cluster.setScore(cluster.getScore() / maxScore);
        }

        // Sort clusters by their score
        Collections.sort(clusters, PropertyHelper
            .getComparatorForDoubleProperty(RawCluster.PROPERTY_SCORE, true));

        // Move all singletons to a single group
        // TODO: reassign to other groups?
        RawClusterBase singletons = new RawClusterBase();
        singletons.addLabel("(Singletons)");
        singletons.setScore(0.0002);
        singletons.setProperty(RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE);
        for (int i = 0; i < clusters.size();)
        {
            RawClusterBase cluster = (RawClusterBase) clusters.get(i);
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
        RawClusterBase otherTopics = new RawClusterBase();
        otherTopics.addLabel("(Other Topics)");
        otherTopics.setScore(0.0001);
        otherTopics.setProperty(RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE);
        int r = 0;
        for (int d = 0; d < tokenizedDocuments.size(); d++)
        {
            TokenizedDocument tokenizedDocument = (TokenizedDocument) tokenizedDocuments
                .get(d);

            if (!assignedDocuments.contains(tokenizedDocument))
            {
                otherTopics.addDocument((RawDocument) tokenizedDocument
                    .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT));

                double score = 0;
                if (tokenizedDocument
                    .getProperty(TdMatrixBuildingStrategy.PROPERTY_DOCUMENT_OMITTED) == null)
                {
                    score = cos.get(r, MatrixUtils.maxInRow(cos, r));
                }

                ((RawDocument) tokenizedDocument
                    .getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT))
                    .setProperty(PROPERTY_CLUSTER_MEMBER_SCORE, new Double(
                        score));
            }

            if (tokenizedDocument
                .getProperty(TdMatrixBuildingStrategy.PROPERTY_DOCUMENT_OMITTED) == null)
            {
                r++;
            }
        }
        // Order documents within the cluster by document score
        Collections.sort(otherTopics.getDocuments(),
            PropertyHelper.getComparatorForDoubleProperty(
                PROPERTY_CLUSTER_MEMBER_SCORE, true));

        if (otherTopics.getDocuments().size() > 0)
        {
            clusters.add(otherTopics);
        }

        return clusters;
    }

    /**
     * @param profile
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

    /**
     * @param component
     * @param time
     */
    private void logMeantime(String component, long time)
    {
        if (profile == null)
        {
            return;
        }

        Map times;
        if (profile.getProfileEntry("subcomponent-times") == null)
        {
            times = new LinkedHashMap();
            profile.addProfileEntry("subcomponent-times", new ProfileEntry(
                "Subcomponent Times", null, times));
        }
        else
        {
            times = (Map) profile.getProfileEntry("subcomponent-times")
                .getData();
        }
        times.put(component, Long.toString(time) + " ms");
    }

    /**
     * @param label
     * @param text
     */
    private void logInfo(String label, Object text)
    {
        if (profile == null)
        {
            return;
        }

        Map info;
        if (profile.getProfileEntry("info") == null)
        {
            info = new LinkedHashMap();
            profile.addProfileEntry("info", new ProfileEntry(
                "Counts and Numbers", null, info));
        }
        else
        {
            info = (Map) profile.getProfileEntry("info").getData();
        }
        info.put(label, text);
    }
}