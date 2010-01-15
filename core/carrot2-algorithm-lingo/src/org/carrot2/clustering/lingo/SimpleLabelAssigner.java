
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import java.util.Arrays;

import org.carrot2.matrix.MatrixUtils;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.Bindable;

import bak.pcj.map.IntIntOpenHashMap;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.jet.math.Functions;

/**
 * Uses a very simple algorithm to match labels to base vectors.
 */
@Bindable
public class SimpleLabelAssigner implements ILabelAssigner
{
    public void assignLabels(LingoProcessingContext context, DoubleMatrix2D stemCos,
        IntIntOpenHashMap filteredRowToStemIndex, DoubleMatrix2D phraseCos)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final int firstPhraseIndex = preprocessingContext.allLabels.firstPhraseIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] mostFrequentOriginalWordIndex = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final int desiredClusterCount = stemCos.columns();

        int [] candidateStemIndices = new int [desiredClusterCount];
        double [] candidateStemScores = new double [desiredClusterCount];
        
        int [] candidatePhraseIndices = new int [desiredClusterCount];
        Arrays.fill(candidatePhraseIndices, -1);
        double [] candidatePhraseScores = new double [desiredClusterCount];

        MatrixUtils.maxInColumns(stemCos, candidateStemIndices, candidateStemScores,
            Functions.abs);

        if (phraseCos != null)
        {
            MatrixUtils.maxInColumns(phraseCos, candidatePhraseIndices,
                candidatePhraseScores, Functions.abs);
        }

        // Choose between single words and phrases for each base vector
        final int [] clusterLabelFeatureIndex = new int [desiredClusterCount];
        double [] clusterLabelScore = new double [desiredClusterCount];
        for (int i = 0; i < desiredClusterCount; i++)
        {
            final int phraseFeatureIndex = candidatePhraseIndices[i];
            final int stemIndex = filteredRowToStemIndex.get(candidateStemIndices[i]);

            final double phraseScore = candidatePhraseScores[i];
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
}
