package org.carrot2.clustering.lingo;

import java.util.Arrays;
import java.util.List;

import org.carrot2.core.attribute.Processing;
import org.carrot2.matrix.MatrixUtils;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.GraphUtils;
import org.carrot2.util.LinearApproximation;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

import bak.pcj.IntIterator;
import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;
import bak.pcj.map.*;
import bak.pcj.set.IntBitSet;
import bak.pcj.set.IntSet;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * Builds cluster labels based on the reduced term-document matrix and assigns documents
 * to the labels.
 */
@Bindable
public class ClusterBuilder
{
    /**
     * Phrase label boost. The weight of multi-word labels relative to one-word labels.
     * Low values will result in more one-word labels being produced, higher values will
     * favor multi-word labels.
     * 
     * @group Labels
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 10.00)
    public double phraseLabelBoost = 1.0;

    /**
     * Phrase length penalty start. The phrase length at which the overlong multi-word
     * labels should start to be penalized. Phrases of length smaller than
     * <code>phraseLengthPenaltyStart</code> will not be penalized.
     * 
     * @group Labels
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 8)
    public int phraseLengthPenaltyStart = 5;

    /**
     * Phrase length penalty stop. The phrase length at which the overlong multi-word
     * labels should be removed completely. Phrases of length larger than
     * <code>phraseLengthPenaltyStop</code> will be removed.
     * 
     * @group Labels
     * @level Advanced
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 2, max = 8)
    public int phraseLengthPenaltyStop = 8;

    /**
     * Cluster merging threshold. The percentage overlap between two cluster's documents
     * required for the clusters to be merged into one clusters. Low values will result in
     * more aggressive merging, which may lead to irrelevant documents in clusters. High
     * values will result in fewer clusters being merged, which may lead to very similar
     * or duplicated clusters.
     * 
     * @group Labels
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    public double clusterMergingThreshold = 0.7;

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
    void buildLabels(LingoProcessingContext context, TermWeighting termWeighting)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final DoubleMatrix2D reducedTdMatrix = context.baseMatrix;
        final int desiredClusterCount = reducedTdMatrix.columns();
        final int [] wordsStemIndex = preprocessingContext.allWords.stemIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] mostFrequentOriginalWordIndex = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final int [][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
        final IntSet [] labelsDocumentIndices = preprocessingContext.allLabels.documentIndices;
        final int wordCount = wordsStemIndex.length;
        final int documentCount = preprocessingContext.documents.size();

        // tdMatrixStemIndex contains individual stems that appeared in AllLabels
        // but also stems that appeared only in phrases from AllLabels, but not
        // as individual stems. For this reason, for matching single word labels
        // we should use only those stems that appeared in AllLabels as one-word
        // candidates.
        final IntBitSet oneWordCandidateStemIndices = new IntBitSet();
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex >= wordCount)
            {
                break;
            }
            oneWordCandidateStemIndices.add(wordsStemIndex[featureIndex]);
        }

        final IntKeyIntMap stemToRowIndex = context.tdMatrixStemToRowIndex;
        final IntKeyIntMap filteredRowToStemIndex = new IntKeyIntOpenHashMap();
        final IntArrayList filteredRows = new IntArrayList();
        int filteredRowIndex = 0;
        for (IntKeyIntMapIterator it = stemToRowIndex.entries(); it.hasNext();)
        {
            it.next();
            if (oneWordCandidateStemIndices.contains(it.getKey()))
            {
                filteredRowToStemIndex.put(filteredRowIndex++, it.getKey());
                filteredRows.add(it.getValue());
            }

        }

        // Find single word label candidates
        int [] candidateStemIndices = new int [desiredClusterCount];
        double [] candidateStemScores = new double [desiredClusterCount];
        MatrixUtils.maxInColumns(reducedTdMatrix.viewSelection(filteredRows.toArray(),
            null), candidateStemIndices, candidateStemScores, Functions.abs);

        // Find multiword label candidates
        int [] candidatePhraseIndices = new int [desiredClusterCount];
        Arrays.fill(candidatePhraseIndices, -1);
        double [] candidatePhraseScores = new double [desiredClusterCount];

        buildPhraseMatrix(context, termWeighting);
        final DoubleMatrix2D phraseMatrix = context.phraseMatrix;
        final int firstPhraseIndex = context.firstPhraseIndex;
        if (phraseMatrix != null)
        {
            // Build raw cosine similarities
            final DoubleMatrix2D phraseCos = phraseMatrix.zMult(reducedTdMatrix, null);

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

                double penalty = documentSizeCoefficients
                    .getValue(labelsDocumentIndices[row + firstPhraseIndex].size()
                        / (double) documentCount);

                if (phraseWordIndices.length >= phraseLengthPenaltyStart)
                {
                    penalty *= 1 - penaltyStep
                        * (phraseWordIndices.length - phraseLengthPenaltyStart + 1);
                }
                phraseCos.viewRow(row).assign(Functions.mult(penalty));
            }

            // Normalize again
            MatrixUtils.normalizeColumnL2(phraseCos, null);

            MatrixUtils.maxInColumns(phraseCos, candidatePhraseIndices,
                candidatePhraseScores, Functions.abs);
        }

        // Choose between single words and phrases for each base vector
        final int [] clusterLabelFeatureIndex = new int [reducedTdMatrix.columns()];
        double [] clusterLabelScore = new double [reducedTdMatrix.columns()];
        for (int i = 0; i < reducedTdMatrix.columns(); i++)
        {
            final int phraseFeatureIndex = candidatePhraseIndices[i];
            final int stemIndex = filteredRowToStemIndex.get(candidateStemIndices[i]);

            final double phraseScore = candidatePhraseScores[i] * phraseLabelBoost;
            if (phraseFeatureIndex >= 0 && phraseScore > candidateStemScores[i])
            {
                clusterLabelFeatureIndex[i] = labelsFeatureIndex[phraseFeatureIndex
                    + firstPhraseIndex];
                clusterLabelScore[i] = phraseScore;
            }
            else
            {
                clusterLabelFeatureIndex[i] = mostFrequentOriginalWordIndex[stemIndex];
                clusterLabelScore[i] = candidateStemScores[i];
            }
        }

        context.clusterLabelFeatureIndex = clusterLabelFeatureIndex;
        context.clusterLabelScore = clusterLabelScore;
    }

    /**
     * Assigns documents to cluster labels.
     */
    void assignDocuments(LingoProcessingContext context, TermWeighting termWeighting)
    {
        final int [] clusterLabelFeatureIndex = context.clusterLabelFeatureIndex;
        final IntSet [] clusterDocuments = new IntBitSet [clusterLabelFeatureIndex.length];

        final int [] labelsFeatureIndex = context.preprocessingContext.allLabels.featureIndex;
        final IntSet [] documentIndices = context.preprocessingContext.allLabels.documentIndices;
        final IntKeyIntMap featureValueToIndex = new IntKeyIntOpenHashMap();

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
        final IntSet [] clusterDocuments = context.clusterDocuments;
        final int [] clusterLabelFeatureIndex = context.clusterLabelFeatureIndex;
        final double [] clusterLabelScore = context.clusterLabelScore;

        final List<IntList> mergedClusters = GraphUtils.findCoherentSubgraphs(
            clusterDocuments.length, new GraphUtils.ArcPredicate()
            {
                private IntBitSet temp = new IntBitSet();

                public boolean isArcPresent(int clusterA, int clusterB)
                {
                    temp.clear();
                    int size;
                    IntSet setA = clusterDocuments[clusterA];
                    IntSet setB = clusterDocuments[clusterB];

                    // Suitable for flat clustering
                    // A small subgroup contained within a bigger group
                    // will give small overlap ratio. Big ratios will
                    // be produced only for balanced group sizes.
                    if (setA.size() < setB.size())
                    {
                        temp.addAll(setA);
                        temp.retainAll(setB);
                        size = setB.size();
                    }
                    else
                    {
                        temp.addAll(setB);
                        temp.retainAll(setA);
                        size = setA.size();
                    }

                    return temp.size() / (double) size >= clusterMergingThreshold;
                }
            }, true);

        // For each merge group, choose the cluster with the highets score and
        // merge the rest to it
        for (IntList clustersToMerge : mergedClusters)
        {
            int mergeBaseClusterIndex = -1;
            double maxScore = -1;
            for (IntIterator it = clustersToMerge.iterator(); it.hasNext();)
            {
                final int clusterIndex = it.next();
                if (clusterLabelScore[clusterIndex] > maxScore)
                {
                    mergeBaseClusterIndex = clusterIndex;
                    maxScore = clusterLabelScore[clusterIndex];
                }
            }

            for (IntIterator it = clustersToMerge.iterator(); it.hasNext();)
            {
                final int clusterIndex = it.next();
                if (clusterIndex != mergeBaseClusterIndex)
                {
                    clusterDocuments[mergeBaseClusterIndex]
                        .addAll(clusterDocuments[clusterIndex]);
                    clusterLabelFeatureIndex[clusterIndex] = -1;
                    clusterDocuments[clusterIndex] = null;
                }

            }
        }
    }

    /**
     * Builds a term-document like matrix for phrases. Returns <code>null</code> if there
     * are no phrases on the input.
     */
    void buildPhraseMatrix(LingoProcessingContext context, TermWeighting termWeighting)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final IntKeyIntMap stemToRowIndex = context.tdMatrixStemToRowIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int wordCount = preprocessingContext.allWords.image.length;

        // Compute first phrase index
        int firstPhraseIndex = -1;
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            if (labelsFeatureIndex[i] >= wordCount)
            {
                firstPhraseIndex = i;
                break;
            }
        }

        context.firstPhraseIndex = firstPhraseIndex;

        if (firstPhraseIndex >= 0 && stemToRowIndex.size() > 0)
        {
            // Build phrase matrix
            int [] phraseFeatureIndices = new int [labelsFeatureIndex.length
                - firstPhraseIndex];
            for (int featureIndex = 0; featureIndex < phraseFeatureIndices.length; featureIndex++)
            {
                phraseFeatureIndices[featureIndex] = labelsFeatureIndex[featureIndex
                    + firstPhraseIndex];
            }

            final DoubleMatrix2D phraseMatrix = TermDocumentMatrixBuilder
                .buildAlignedMatrix(context, phraseFeatureIndices, termWeighting);
            MatrixUtils.normalizeColumnL2(phraseMatrix, null);
            context.phraseMatrix = phraseMatrix.viewDice();
        }
    }
}
