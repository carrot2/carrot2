
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import java.util.Arrays;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.matrix.*;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.vsm.ITermWeighting;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import org.carrot2.util.GraphUtils;
import org.carrot2.util.LinearApproximation;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.*;

import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.cursors.IntIntCursor;

/**
 * Builds cluster labels based on the reduced term-document matrix and assigns documents
 * to the labels.
 */
@Bindable(prefix = "LingoClusteringAlgorithm")
public class ClusterBuilder
{
    /**
     * Phrase label boost. The weight of multi-word labels relative to one-word labels.
     * Low values will result in more one-word labels being produced, higher values will
     * favor multi-word labels.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 10.00)
    @Group(DefaultGroups.LABELS)
    @Level(AttributeLevel.MEDIUM)
    @Label("Phrase label boost")    
    public double phraseLabelBoost = 1.5;

    /**
     * Phrase length penalty start. The phrase length at which the overlong multi-word
     * labels should start to be penalized. Phrases of length smaller than
     * <code>phraseLengthPenaltyStart</code> will not be penalized.
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 8)
    @Group(DefaultGroups.LABELS)
    @Level(AttributeLevel.ADVANCED)
    @Label("Phrase length penalty start")    
    public int phraseLengthPenaltyStart = 8;

    /**
     * Phrase length penalty stop. The phrase length at which the overlong multi-word
     * labels should be removed completely. Phrases of length larger than
     * <code>phraseLengthPenaltyStop</code> will be removed.
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 8)
    @Group(DefaultGroups.LABELS)
    @Level(AttributeLevel.ADVANCED)
    @Label("Phrase length penalty stop")    
    public int phraseLengthPenaltyStop = 8;

    /**
     * Cluster merging threshold. The percentage overlap between two cluster's documents
     * required for the clusters to be merged into one clusters. Low values will result in
     * more aggressive merging, which may lead to irrelevant documents in clusters. High
     * values will result in fewer clusters being merged, which may lead to very similar
     * or duplicated clusters.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    @Group(DefaultGroups.CLUSTERS)
    @Level(AttributeLevel.MEDIUM)
    @Label("Cluster merging threshold")        
    public double clusterMergingThreshold = 0.7;

    /**
     * Optional feature scorer. We don't make it an attribute for now as the core Lingo
     * will not have any implementations for this interface.
     */
    public IFeatureScorer featureScorer = null;

    /**
     * Cluster label assignment method.
     */
    @Input
    @Processing
    @Attribute
    @Required
    @ImplementingClasses(classes =
    {
        UniqueLabelAssigner.class, SimpleLabelAssigner.class
    })
    @Group(DefaultGroups.LABELS)
    @Level(AttributeLevel.ADVANCED)
    @Label("Cluster label assignment method")                
    public ILabelAssigner labelAssigner = new UniqueLabelAssigner();

    /**
     * Coefficients for label weighting based on the cluster size.
     */
    private LinearApproximation documentSizeCoefficients = new LinearApproximation(
        new double []
        {
            1.0, 1.5, 1.3, 0.9, 0.7, 0.6, 0.3, 0.05, 0.05, 0.05, 0.05
        }, 0.0, 1.0);

    /**
     * Discovers labels for clusters.
     */
    void buildLabels(LingoProcessingContext context, ITermWeighting termWeighting)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final VectorSpaceModelContext vsmContext = context.vsmContext;
        final DoubleMatrix2D reducedTdMatrix = context.reducedVsmContext.baseMatrix;
        final int [] wordsStemIndex = preprocessingContext.allWords.stemIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] mostFrequentOriginalWordIndex = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final int [][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
        final BitSet [] labelsDocumentIndices = preprocessingContext.allLabels.documentIndices;
        final int wordCount = preprocessingContext.allWords.image.length;
        final int documentCount = preprocessingContext.documents.size();

        // tdMatrixStemIndex contains individual stems that appeared in AllLabels
        // but also stems that appeared only in phrases from AllLabels, but not
        // as individual stems. For this reason, for matching single word labels
        // we should use only those stems that appeared in AllLabels as one-word
        // candidates.
        final BitSet oneWordCandidateStemIndices = new BitSet();
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex >= wordCount)
            {
                break;
            }
            oneWordCandidateStemIndices.set(wordsStemIndex[featureIndex]);
        }

        final IntIntHashMap stemToRowIndex = vsmContext.stemToRowIndex;
        final IntIntHashMap filteredRowToStemIndex = new IntIntHashMap();
        final IntArrayList filteredRows = new IntArrayList();
        int filteredRowIndex = 0;
        for (IntIntCursor it : stemToRowIndex)
        {
            if (oneWordCandidateStemIndices.get(it.key))
            {
                filteredRowToStemIndex.put(filteredRowIndex++, it.key);
                filteredRows.add(it.value);
            }
        }

        // Request additional feature scores
        final double [] featureScores = featureScorer != null ? featureScorer
            .getFeatureScores(context) : null;
        final int [] wordLabelIndex = new int [wordCount];

        // Word index to feature index mapping
        Arrays.fill(wordLabelIndex, -1);
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex < wordCount)
            {
                wordLabelIndex[featureIndex] = i;
            }
        }

        // Prepare base vector -- single stem cosine matrix.
        final DoubleMatrix2D stemCos = reducedTdMatrix.viewSelection(
            filteredRows.toArray(), null).copy();
        for (int r = 0; r < stemCos.rows(); r++)
        {
            final int labelIndex = wordLabelIndex[mostFrequentOriginalWordIndex[filteredRowToStemIndex
                .get(r)]];
            double penalty = getDocumentCountPenalty(labelIndex, documentCount,
                labelsDocumentIndices);
            if (featureScores != null)
            {
                penalty *= featureScores[labelIndex];
            }

            stemCos.viewRow(r).assign(Functions.mult(penalty));
        }

        // Prepare base vector -- phrase cosine matrix
        final DoubleMatrix2D phraseMatrix = vsmContext.termPhraseMatrix;
        final int firstPhraseIndex = preprocessingContext.allLabels.firstPhraseIndex;
        DoubleMatrix2D phraseCos = null;
        if (phraseMatrix != null)
        {
            // Build raw cosine similarities
            phraseCos = phraseMatrix.zMult(reducedTdMatrix, null, 1, 0, false, false);

            // Apply phrase weighting
            if (phraseLengthPenaltyStop < phraseLengthPenaltyStart)
            {
                phraseLengthPenaltyStop = phraseLengthPenaltyStart;
            }
            final double penaltyStep = 1.0 / (phraseLengthPenaltyStop
                - phraseLengthPenaltyStart + 1);

            // Multiply each row of the cos matrix (corresponding to the phrase) by the
            // penalty factor, if the phrase is longer than penalty start length
            for (int row = 0; row < phraseCos.rows(); row++)
            {
                final int phraseFeature = labelsFeatureIndex[row + firstPhraseIndex];
                int [] phraseWordIndices = phrasesWordIndices[phraseFeature - wordCount];

                double penalty;
                if (phraseWordIndices.length >= phraseLengthPenaltyStop)
                {
                    penalty = 0;
                }
                else
                {
                    penalty = getDocumentCountPenalty(row + firstPhraseIndex,
                        documentCount, labelsDocumentIndices);

                    if (phraseWordIndices.length >= phraseLengthPenaltyStart)
                    {
                        penalty *= 1 - penaltyStep
                            * (phraseWordIndices.length - phraseLengthPenaltyStart + 1);
                    }
                    if (featureScores != null)
                    {
                        penalty *= featureScores[row + firstPhraseIndex];
                    }
                }
                phraseCos.viewRow(row).assign(Functions.mult(penalty * phraseLabelBoost));
            }
        }

        // Assign labels to base vectors
        labelAssigner.assignLabels(context, stemCos, filteredRowToStemIndex, phraseCos);
    }

    private double getDocumentCountPenalty(int labelIndex, int documentCount,
        BitSet [] labelsDocumentIndices)
    {
        return documentSizeCoefficients.getValue(
            labelsDocumentIndices[labelIndex].cardinality() / (double) documentCount);
    }

    /**
     * Assigns documents to cluster labels.
     */
    void assignDocuments(LingoProcessingContext context)
    {
        final int [] clusterLabelFeatureIndex = context.clusterLabelFeatureIndex;
        final BitSet [] clusterDocuments = new BitSet [clusterLabelFeatureIndex.length];

        final int [] labelsFeatureIndex = context.preprocessingContext.allLabels.featureIndex;
        final BitSet [] documentIndices = context.preprocessingContext.allLabels.documentIndices;
        final IntIntHashMap featureValueToIndex = new IntIntHashMap();

        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            featureValueToIndex.put(labelsFeatureIndex[i], i);
        }

        for (int clusterIndex = 0; clusterIndex < clusterDocuments.length; clusterIndex++)
        {
            clusterDocuments[clusterIndex] = documentIndices[featureValueToIndex
                .get(clusterLabelFeatureIndex[clusterIndex])];
        }

        context.clusterDocuments = clusterDocuments;
    }

    /**
     * Merges overlapping clusters. Stores merged label and documents in the relevant
     * arrays of the merged cluster, sets scores to -1 in those clusters that got merged.
     */
    void merge(LingoProcessingContext context)
    {
        final BitSet [] clusterDocuments = context.clusterDocuments;
        final int [] clusterLabelFeatureIndex = context.clusterLabelFeatureIndex;
        final double [] clusterLabelScore = context.clusterLabelScore;

        final List<IntArrayList> mergedClusters = GraphUtils.findCoherentSubgraphs(
            clusterDocuments.length, new GraphUtils.IArcPredicate()
            {
                private BitSet temp = new BitSet();

                public boolean isArcPresent(int clusterA, int clusterB)
                {
                    temp.clear();
                    int size;
                    BitSet setA = clusterDocuments[clusterA];
                    BitSet setB = clusterDocuments[clusterB];

                    // Suitable for flat clustering
                    // A small subgroup contained within a bigger group
                    // will give small overlap ratio. Big ratios will
                    // be produced only for balanced group sizes.
                    if (setA.cardinality() < setB.cardinality())
                    {
                        // addAll == or
                        // reiatinAll == and | intersect
                        temp.or(setA);
                        temp.intersect(setB);
                        size = (int) setB.cardinality();
                    }
                    else
                    {
                        temp.or(setB);
                        temp.intersect(setA);
                        size = (int) setA.cardinality();
                    }

                    return temp.cardinality() / (double) size >= clusterMergingThreshold;
                }
            }, true);

        
        // For each merge group, choose the cluster with the highest score and
        // merge the rest to it
        for (IntArrayList clustersToMerge : mergedClusters)
        {
            int mergeBaseClusterIndex = -1;
            double maxScore = -1;
            
            final int [] buf = clustersToMerge.buffer;
            final int max = clustersToMerge.size();
            for (int i = 0; i < max; i++)
            {
                final int clusterIndex = buf[i];
                if (clusterLabelScore[clusterIndex] > maxScore)
                {
                    mergeBaseClusterIndex = clusterIndex;
                    maxScore = clusterLabelScore[clusterIndex];
                }
            }

            for (int i = 0; i < max; i++)
            {
                final int clusterIndex = buf[i];
                if (clusterIndex != mergeBaseClusterIndex)
                {
                    clusterDocuments[mergeBaseClusterIndex].or(
                        clusterDocuments[clusterIndex]);
                    clusterLabelFeatureIndex[clusterIndex] = -1;
                    clusterDocuments[clusterIndex] = null;
                }

            }
        }
    }
}
