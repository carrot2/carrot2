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

import cern.colt.list.*;
import cern.colt.matrix.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.lingo.model.*;
import com.stachoodev.matrix.factorization.*;
import com.stachoodev.util.common.*;

/**
 * Implementation of the Lingo algorithm.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoWeb
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

    /** Model builder context */
    private ModelBuilderContext modelBuilderContext;

    /** A list of tokenized documents */
    private List tokenizedDocuments;

    /** An algorithm for feature selection */
    private FeatureSelection featureSelectionStrategy;

    /** Phrase extraction strategy to be used */
    private PhraseExtraction phraseExtractionStrategy;

    /** An algorithm for creating the term-document matrix */
    private TdMatrixBuilding tdMatrixBuildingStrategy;

    /** */
    private PhraseMatrixBuilding phraseMatrixBuilding;

    /** Matrix factorization to be used for cluster discovery */
    private MatrixFactorizationFactory matrixFactorizationFactory;

    /** Selected single terms */
    private List selectedTerms;

    /** Frequent phrases */
    private List frequentPhrases;

    /** Term-document matrix */
    private DoubleMatrix2D masterTdMatrix;

    /** Phrase matrix */
    private DoubleMatrix2D masterPhraseMatrix;

    /** Balances between clustering quality an execution time */
    private int qualityLevel;

    /** Execution profile */
    private Profile profile;

    /** Raw query */
    private String rawQuery;

    /**
     * Creates an instance of Lingo with default parameters.
     */
    public LingoWeb()
    {
        this(new HashMap());
    }

    /**
     * Creates an instance of Lingo with given parameters. See descriptions of
     * constants for possible parameter names and values.
     * 
     * @param parameters
     */
    public LingoWeb(Map parameters)
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
            this.featureSelectionStrategy = (FeatureSelection) parameters
                .get(PARAMETER_FEATURE_SELECTION_STRATEGY);
        }
        else
        {
            this.featureSelectionStrategy = new TfFeatureSelection();
            ((FeatureSelectionBase) featureSelectionStrategy)
                .setDoubleProperty(TfFeatureSelection.PROPERTY_TF_THRESHOLD, 3);
            ((FeatureSelectionBase) featureSelectionStrategy)
                .setDoubleProperty(
                    TfFeatureSelection.PROPERTY_TITLE_TF_MULTIPLIER, 1.5);
        }

        // Set phrase extraction
        if (parameters.get(PARAMETER_PHRASE_EXTRACTION_STRATEGY) != null)
        {
            this.phraseExtractionStrategy = (PhraseExtraction) parameters
                .get(PARAMETER_PHRASE_EXTRACTION_STRATEGY);
        }
        else
        {
            this.phraseExtractionStrategy = new SAPhraseExtraction();
        }

        // Set td matrix building
        if (parameters.get(PARAMETER_TD_MATRIX_BUILDING_STRATEGY) != null)
        {
            this.tdMatrixBuildingStrategy = (TdMatrixBuilding) parameters
                .get(PARAMETER_TD_MATRIX_BUILDING_STRATEGY);
        }
        else
        {
            this.tdMatrixBuildingStrategy = new TfIdfTdMatrixBuilding();
            ((TdMatrixBuildingBase) tdMatrixBuildingStrategy)
                .setDoubleProperty(
                    TfIdfTdMatrixBuilding.PROPERTY_TITLE_TF_MULTIPLIER, 1.5);
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

        // Phrase matrix builder
        phraseMatrixBuilding = new PhraseMatrixBuilding();

        // Create model builder context
        modelBuilderContext = new ModelBuilderContext();
    }

    /**
     * @param query
     * @param rawDocuments
     * @return
     */
    public List cluster(List tokenizedDocuments, String query)
    {
        long start = System.currentTimeMillis();
        long stop = 0;

        this.tokenizedDocuments = tokenizedDocuments;
        this.rawQuery = query;

        if (prepare())
        {
            stop = System.currentTimeMillis();
            LingoProfilingUtils.logMeantime(profile, "Prepare Total", stop
                - start);

            // Create top cluster structure
            start = System.currentTimeMillis();
            LingoWorker lingoWorker = new LingoWorker(selectedTerms,
                frequentPhrases, masterTdMatrix, masterPhraseMatrix,
                modelBuilderContext, profile);
            LingoCluster sourceCluster = createSourceCluster();

            // Create minor clusters
            List topClusters = doWork(2 + sourceCluster.getDocumentIndices().size()/150, 0, lingoWorker, sourceCluster);

            stop = System.currentTimeMillis();
            LingoProfilingUtils.logMeantime(profile, "Lingo Workers Top", stop
                - start);

            // Create subclusters
            start = System.currentTimeMillis();
            for (Iterator iter = topClusters.iterator(); iter.hasNext();)
            {
                LingoCluster lingoCluster = (LingoCluster) iter.next();
                if (!lingoCluster.isJunkCluster()
                    && lingoCluster.getMembers().size() >= 10)
                {
                    List subClusters = doWork(1 + (lingoCluster.getMembers()
                        .size() >= 25 ? 1 : 0), 1, lingoWorker, lingoCluster);

                    if (subClusters.size() != 0)
                    {
                        lingoCluster.addSubclusters(subClusters);
                        lingoCluster.clearMembers();
                    }
                }
            }

            stop = System.currentTimeMillis();
            LingoProfilingUtils.logMeantime(profile, "Lingo Workers Sub", stop
                - start);

            // Form final clusters
            List majorClusters = formFinalClusters(topClusters);

            // Log some info
            logInfo();

            return majorClusters;
        }
        else
        {
            return new ArrayList();
        }
    }

    /**
     * @param passCount
     * @param level
     * @param lingoWorker
     * @param sourceCluster
     * @return
     */
    private List doWork(int passCount, int level, LingoWorker lingoWorker,
        LingoCluster sourceCluster)
    {
        List previousMajorClusters = new ArrayList();
        for (int i = 0; i < passCount; i++)
        {
            List minorLingoClusters = lingoWorker.createMinorClusters(
                sourceCluster, previousMajorClusters, i, level, 2);
            if (minorLingoClusters.size() == 0)
            {
                break;
            }

            previousMajorClusters.addAll(minorLingoClusters);

            int junkClusterSize = mergeResultClusters(previousMajorClusters);
            if (junkClusterSize
                / (double) sourceCluster.getDocumentIndices().size() < 0.3
                && i >= 1)
            {
                break;
            }
        }

        Collections.sort(previousMajorClusters);
        return previousMajorClusters;
    }

    /**
     * Note: the (Other Topics) group is not sorted at all
     * 
     * @param lingoClusters
     */
    private int mergeResultClusters(List lingoClusters)
    {
        // Merge junk clusters
        List junkDocumentIndices = new ArrayList();
        for (int i = lingoClusters.size() - 1; i >= 0; i--)
        {
            LingoCluster lingoCluster = (LingoCluster) lingoClusters.get(i);
            if (lingoCluster.isJunkCluster())
            {
                junkDocumentIndices.add(lingoCluster.getDocumentIndices());
                lingoClusters.remove(i);
            }
        }

        IntArrayList union = union(junkDocumentIndices);

        if (union.size() > 0)
        {
            LingoCluster otherTopics = new LingoCluster(null,
                new LingoClusterLabel("(Other Topics)", null), true);
            for (int i = 0; i < union.size(); i++)
            {
                otherTopics.addMember(new LingoClusterMember(union.get(i), 0));
            }
            lingoClusters.add(otherTopics);
        }

        return union.size();
    }

    /**
     * @param lists
     * @return
     */
    private IntArrayList union(List lists)
    {
        if (lists.size() == 1)
        {
            return (IntArrayList) lists.get(0);
        }

        IntArrayList result = new IntArrayList();
        if (lists.size() == 0)
        {
            return result;
        }

        // Sort the lists
        for (int j = 1; j < lists.size(); j++)
        {
            IntArrayList otherList = (IntArrayList) lists.get(j);
            otherList.sort();
        }

        IntArrayList intList = (IntArrayList) lists.get(0);
        for (int i = 0; i < intList.size(); i++)
        {
            boolean include = true;
            for (int j = 1; j < lists.size(); j++)
            {
                IntArrayList otherList = (IntArrayList) lists.get(j);
                if (otherList.binarySearch(intList.get(i)) < 0)
                {
                    include = false;
                    break;
                }
            }

            if (include)
            {
                result.add(intList.get(i));
            }
        }

        return result;
    }

    /**
     * @param lingoClusters
     * @return
     */
    private List formFinalClusters(List lingoClusters)
    {
        List clusters = new ArrayList();

        for (Iterator iter = lingoClusters.iterator(); iter.hasNext();)
        {
            LingoCluster lingoCluster = (LingoCluster) iter.next();
            RawClusterBase rawCluster = new RawClusterBase();

            rawCluster.addLabel(lingoCluster.getLabel().getImage());

            // Add subclusters
            List subClusters = formFinalClusters(lingoCluster.getSubClusters());
            for (Iterator iterator = subClusters.iterator(); iterator.hasNext();)
            {
                RawCluster rawSubCluster = (RawCluster) iterator.next();
                rawCluster.addSubcluster(rawSubCluster);
            }

            // Add members
            List members = lingoCluster.getMembers();
            for (Iterator iterator = members.iterator(); iterator.hasNext();)
            {
                LingoClusterMember member = (LingoClusterMember) iterator
                    .next();
                RawDocument rawDocument = (RawDocument) ((TokenizedDocument) tokenizedDocuments
                    .get(member.getDocumentIndex()))
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
                rawDocument.setProperty(PROPERTY_CLUSTER_MEMBER_SCORE,
                    new Double(member.getScore()));

                rawCluster.addDocument(rawDocument);
            }

            clusters.add(rawCluster);
        }

        return clusters;
    }

    /**
     * @return
     */
    private LingoCluster createSourceCluster()
    {
        // Prepare label consisting of query words
        int [] queryWordCodes = null;
        if (modelBuilderContext.getIntWrapper().getQueryWordCodes() != null)
        {
            queryWordCodes = new int [modelBuilderContext.getIntWrapper()
                .getQueryWordCodes().size()];
            System.arraycopy(modelBuilderContext.getIntWrapper()
                .getQueryWordCodes().elements(), 0, queryWordCodes, 0,
                queryWordCodes.length);
        }
        LingoClusterLabel label = new LingoClusterLabel(rawQuery,
            queryWordCodes);

        // Prepare a list of document indices
        IntArrayList documentIndices = new IntArrayList(tokenizedDocuments
            .size());
        for (int i = 0; i < tokenizedDocuments.size(); i++)
        {
            documentIndices.add(i);
        }

        return new LingoCluster(null, label, false, documentIndices);
    }

    /**
     *  
     */
    public void clear()
    {
        if (phraseExtractionStrategy instanceof SAPhraseExtraction)
        {
            ((SAPhraseExtraction) phraseExtractionStrategy).clear();
        }

        tokenizedDocuments = null;
        selectedTerms = null;
        frequentPhrases = null;

        masterTdMatrix = null;
        profile = null;
    }

    /**
     *  
     */
    private void logInfo()
    {
        LingoProfilingUtils.logInfo(profile, "Selected Terms", Integer
            .toString(selectedTerms.size()));
        LingoProfilingUtils.logInfo(profile, "Extracted Phrases", Integer
            .toString(frequentPhrases.size()));
        LingoProfilingUtils.logInfo(profile, "Term Document Matrix Dimensions",
            masterTdMatrix.rows() + " x " + masterTdMatrix.columns());

        if (profile != null)
        {
            //            List rowLabels = new ArrayList();
            //            rowLabels.addAll(selectedTerms);
            //            rowLabels.addAll(frequentPhrases);
            //            DoubleMatrix2DWrapper wrapper = new DoubleMatrix2DWrapper(
            //                phraseCos, rowLabels, null);
            //            profile.addProfileEntry("phrase-matching", new ProfileEntry(
            //                "Phrase Matching Matrix", null, wrapper));

            Comparator comparator = PropertyHelper
                .getComparatorForDoubleProperty(ExtendedToken.PROPERTY_TF, true);
            Collections.sort(selectedTerms, comparator);
            profile.addProfileEntry("selected-terms", new ProfileEntry(
                "Selected Terms", null, selectedTerms));

            comparator = PropertyHelper.getComparatorForDoubleProperty(
                ExtendedTokenSequence.PROPERTY_TF, true);
            Collections.sort(frequentPhrases, comparator);
            profile.addProfileEntry("extracted-phrases", new ProfileEntry(
                "Extracted Phrases", null, frequentPhrases));
        }
    }

    /**
     * Performs steps that need to be done only once, at the beginning of the
     * algorithm.
     */
    private boolean prepare()
    {
        long start = 0;
        long stop = 0;

        start = System.currentTimeMillis();
        modelBuilderContext.initialize(tokenizedDocuments, rawQuery);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Model Context Init", stop
            - start);

        start = System.currentTimeMillis();
        selectedTerms = featureSelectionStrategy
            .getSelectedFeatures(modelBuilderContext);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Feature Selection", stop
            - start);

        if (selectedTerms.size() == 0)
        {
            // No frequent terms - don't bother to go any further
            return false;
        }

        start = System.currentTimeMillis();
        masterTdMatrix = tdMatrixBuildingStrategy
            .getTdMatrix(modelBuilderContext);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Term-Document Matrix", stop
            - start);

        start = System.currentTimeMillis();
        frequentPhrases = phraseExtractionStrategy
            .getExtractedPhrases(modelBuilderContext);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Phrase Extraction", stop
            - start);

        start = System.currentTimeMillis();
        masterPhraseMatrix = phraseMatrixBuilding
            .getPhraseMatrix(modelBuilderContext);
        stop = System.currentTimeMillis();
        LingoProfilingUtils.logMeantime(profile, "Phrase Matrix Building", stop
            - start);

        return true;
    }

    /**
     * @param profile
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }
}