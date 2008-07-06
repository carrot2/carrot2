package org.carrot2.clustering.lingo;

import org.apache.commons.lang.ArrayUtils;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.util.DoubleComparators;
import org.carrot2.text.util.IndirectSorter;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

import bak.pcj.set.IntBitSet;
import cern.colt.GenericPermuting;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Builds a term document matrix based on the provided {@link PreprocessingContext}.
 */
@Bindable
public class TermDocumentMatrixBuilder
{
    /**
     * Term weighting.
     */
    @Input
    @Processing
    @Attribute
    @ImplementingClasses(classes =
    {
        LogTfIdfTermWeighting.class, LinearTfIdfTermWeighting.class,
        TfTermWeighting.class
    })
    public TermWeighting termWeighting = new LogTfIdfTermWeighting();

    /**
     * Maximum matrix size.
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 50 * 100)
    public int maximumMatrixSize = 250 * 150;

    /**
     * Builds a term document matrix from data provided in the <code>context</code>,
     * stores the result in there.
     */
    void build(LingoProcessingContext lingoContext)
    {
        final PreprocessingContext preprocessingContext = lingoContext.preprocessingContext;

        final int documentCount = preprocessingContext.documents.size();
        final int [] stemsTf = preprocessingContext.allStems.tf;
        final int [][] stemsTfByDocument = preprocessingContext.allStems.tfByDocument;

        if (documentCount == 0)
        {
            lingoContext.tdMatrix = DoubleFactory2D.dense.make(0, 0);
            lingoContext.tdMatrixStemIndices = new int [0];
            return;
        }

        // Determine the stems we, ideally, should include in the matrix
        int [] stemsToInclude = computeRequiredStemIndices(preprocessingContext);

        // Sort stems by weight, so that stems get included in the matrix in the order
        // of frequency
        final double [] stemsWeight = new double [stemsToInclude.length];
        for (int i = 0; i < stemsToInclude.length; i++)
        {
            stemsWeight[i] = termWeighting.calculateTermWeight(
                stemsTf[stemsToInclude[i]],
                stemsTfByDocument[stemsToInclude[i]].length / 2, documentCount);
        }
        final int [] stemWeightOrder = IndirectSorter.sort(stemsWeight,
            DoubleComparators.REVERSED_ORDER);

        // Calculate the number of terms we can include to fulfill the max matrix size
        final int maxRows = maximumMatrixSize / documentCount;
        final DoubleMatrix2D tdMatrix = DoubleFactory2D.dense.make(Math.min(maxRows,
            stemsToInclude.length), documentCount);

        for (int i = 0; i < stemWeightOrder.length && i < maxRows; i++)
        {
            final int stemIndex = stemsToInclude[stemWeightOrder[i]];
            final int [] tfByDocument = stemsTfByDocument[stemIndex];
            final int df = tfByDocument.length / 2;

            int tfByDocumentIndex = 0;
            for (int documentIndex = 0; documentIndex < documentCount; documentIndex++)
            {
                if (tfByDocumentIndex * 2 < tfByDocument.length
                    && tfByDocument[tfByDocumentIndex * 2] == documentIndex)
                {
                    final double weight = termWeighting.calculateTermWeight(
                        tfByDocument[tfByDocumentIndex * 2 + 1], df, documentCount);
                    tfByDocumentIndex++;

                    tdMatrix.set(i, documentIndex, weight);
                }
            }
        }

        // Convert stemsToInclude into tdMatrixStemIndices
        GenericPermuting.permute(stemsToInclude, stemWeightOrder);

        // Store the results
        lingoContext.tdMatrix = tdMatrix;
        lingoContext.tdMatrixStemIndices = ArrayUtils.subarray(stemsToInclude, 0,
            tdMatrix.rows());
    }

    /**
     * Computes stem indices of words that are one-word label candiates or are non-stop
     * words from phrase label candidates.
     */
    private int [] computeRequiredStemIndices(PreprocessingContext context)
    {
        final int [] labelsFeatureIndex = context.allLabels.featureIndex;
        final int [] wordsStemIndex = context.allWords.stemIndex;
        final boolean [] wordsCommonTermFlag = context.allWords.commonTermFlag;
        final int [][] phrasesWordIndices = context.allPhrases.wordIndices;
        final int wordCount = wordsStemIndex.length;

        final IntBitSet requiredStemIndices = new IntBitSet(labelsFeatureIndex.length);

        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            final int featureIndex = labelsFeatureIndex[i];
            if (featureIndex < wordCount)
            {
                requiredStemIndices.add(wordsStemIndex[featureIndex]);
            }
            else
            {
                final int [] wordIndices = phrasesWordIndices[featureIndex - wordCount];
                for (int j = 0; j < wordIndices.length; j++)
                {
                    final int wordIndex = wordIndices[j];
                    if (!wordsCommonTermFlag[wordIndex])
                    {
                        requiredStemIndices.add(wordsStemIndex[wordIndex]);
                    }
                }
            }
        }

        return requiredStemIndices.toArray();
    }
}
