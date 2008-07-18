package org.carrot2.clustering.lingo;

import java.util.Arrays;

import org.carrot2.matrix.MatrixUtils;
import org.carrot2.matrix.NNIDoubleFactory2D;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.Bindable;

import bak.pcj.list.IntArrayList;
import bak.pcj.map.IntKeyIntMap;
import bak.pcj.map.IntKeyIntOpenHashMap;
import bak.pcj.set.IntBitSet;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;

/**
 * Builds cluster label based on the reduced term-document matrix.
 */
@Bindable
public class ClusterLabelBuilder
{
    void buildLabels(LingoProcessingContext context, TermWeighting termWeighting)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final DoubleMatrix2D reducedTdMatrix = context.baseMatrix;
        final int desiredClusterCount = reducedTdMatrix.columns();
        final int [] wordsStemIndex = preprocessingContext.allWords.stemIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] mostFrequentOriginalWordIndex = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final int [][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
        final int wordCount = wordsStemIndex.length;

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

        final IntArrayList oneWordStemCandidateRows = new IntArrayList();
        final int [] tdMatrixStemIndex = context.tdMatrixStemIndex;
        final IntKeyIntMap filteredRowToStemIndex = new IntKeyIntOpenHashMap();
        int rowIndex = 0;
        for (int i = 0; i < tdMatrixStemIndex.length; i++)
        {
            final int stemIndex = tdMatrixStemIndex[i];
            if (oneWordCandidateStemIndices.contains(stemIndex))
            {
                oneWordStemCandidateRows.add(i);
                filteredRowToStemIndex.put(rowIndex++, stemIndex);
            }
        }

        // Find single word label candidates
        int [] candidateStemIndices = new int [desiredClusterCount];
        double [] candidateStemScores = new double [desiredClusterCount];
        MatrixUtils.maxInColumns(reducedTdMatrix.viewSelection(oneWordStemCandidateRows
            .toArray(), null), candidateStemIndices, candidateStemScores, Functions.abs);

        // Find multiword label candidates
        int [] candidatePhraseIndices = new int [desiredClusterCount];
        Arrays.fill(candidatePhraseIndices, -1);
        double [] candidatePhraseScores = new double [desiredClusterCount];

        buildPhraseMatrix(context, termWeighting);
        final DoubleMatrix2D phraseMatrix = context.phraseMatrix;
        final int firstPhraseIndex = context.firstPhraseIndex;
        if (phraseMatrix != null)
        {
            final DoubleMatrix2D phraseCos = phraseMatrix.zMult(reducedTdMatrix, null);
            MatrixUtils.maxInColumns(phraseCos, candidatePhraseIndices,
                candidatePhraseScores, Functions.abs);
        }

        // Choose between single words and phrases for each base vector
        final int [] clusterLabelFeatureIndex = new int [reducedTdMatrix.columns()];
        double [] clusterLabelScore = new double [reducedTdMatrix.columns()];
        for (int i = 0; i < reducedTdMatrix.columns(); i++)
        {

            // Check if the single word is contained in the phrase
            boolean stemInPhrase = false;
            final int phraseFeatureIndex = candidatePhraseIndices[i];
            final int stemIndex = filteredRowToStemIndex.get(candidateStemIndices[i]);
            if (phraseFeatureIndex >= 0)
            {
                final int phraseFeature = labelsFeatureIndex[phraseFeatureIndex
                    + firstPhraseIndex];
                int [] phraseWordIndices = phrasesWordIndices[phraseFeature - wordCount];
                for (int j = 0; j < phraseWordIndices.length; j++)
                {
                    if (stemIndex == wordsStemIndex[phraseWordIndices[j]])
                    {
                        stemInPhrase = true;
                        break;
                    }
                }
            }

            final double phraseScore;
            if (stemInPhrase)
            {
                phraseScore = candidatePhraseScores[i];
            }
            else
            {
                phraseScore = candidatePhraseScores[i];
            }

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
     * Builds a term-document like matrix for phrases. Returns <code>null</code> if there
     * are no phrases on the input.
     */
    void buildPhraseMatrix(LingoProcessingContext context, TermWeighting termWeighting)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final int [] tdMatrixStemIndices = context.tdMatrixStemIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] wordsStemIndex = preprocessingContext.allWords.stemIndex;
        final int [] stemsTf = preprocessingContext.allStems.tf;
        final int [][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;
        final int [][] phrasesWordIndices = preprocessingContext.allPhrases.wordIndices;
        final int wordCount = preprocessingContext.allWords.image.length;
        final int documentCount = preprocessingContext.documents.size();

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

        if (firstPhraseIndex >= 0 && tdMatrixStemIndices.length > 0)
        {
            // Build a stemIndex to the corresponding row index
            final IntKeyIntMap stemToRowIndex = new IntKeyIntOpenHashMap();
            for (int i = 0; i < tdMatrixStemIndices.length; i++)
            {
                stemToRowIndex.put(tdMatrixStemIndices[i], i);
            }

            // Build phrase matrix
            final DoubleMatrix2D phraseMatrix = NNIDoubleFactory2D.nni.make(
                labelsFeatureIndex.length - firstPhraseIndex, tdMatrixStemIndices.length);
            for (int featureIndex = firstPhraseIndex; featureIndex < labelsFeatureIndex.length; featureIndex++)
            {
                final int [] wordIndices = phrasesWordIndices[labelsFeatureIndex[featureIndex]
                    - wordCount];
                for (int wordIndex = 0; wordIndex < wordIndices.length; wordIndex++)
                {
                    final int stemIndex = wordsStemIndex[wordIndices[wordIndex]];
                    if (stemToRowIndex.containsKey(stemIndex))
                    {
                        final int columnIndex = stemToRowIndex.lget();

                        double weight = termWeighting.calculateTermWeight(
                            stemsTf[stemIndex], stemsTfByDocument[stemIndex].length / 2,
                            documentCount);

                        phraseMatrix.setQuick(featureIndex - firstPhraseIndex,
                            columnIndex, weight);
                    }
                }
            }
            MatrixUtils.normalizeColumnL2(phraseMatrix.viewDice(), null);

            context.phraseMatrix = phraseMatrix;
        }
    }
}
