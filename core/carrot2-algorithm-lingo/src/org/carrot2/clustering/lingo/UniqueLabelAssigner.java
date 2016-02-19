
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

import org.carrot2.mahout.math.matrix.*;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.Pair;
import org.carrot2.util.attribute.Bindable;

import com.carrotsearch.hppc.*;

/**
 * Assigns unique labels to each base vector using a greedy algorithm. For each base
 * vector chooses the label that maximizes the base vector--label term vector cosine
 * similarity and has not been previously selected. Once a label is selected, it will not
 * be used to label any other vector. This algorithm does not create duplicate cluster
 * labels, which usually means that this assignment method will create more clusters than
 * {@link SimpleLabelAssigner}. This method is slightly slower than
 * {@link SimpleLabelAssigner}.
 */
@Bindable
public class UniqueLabelAssigner implements ILabelAssigner
{
    public void assignLabels(LingoProcessingContext context, DoubleMatrix2D stemCos,
        IntIntHashMap filteredRowToStemIndex, DoubleMatrix2D phraseCos)
    {
        final PreprocessingContext preprocessingContext = context.preprocessingContext;
        final int firstPhraseIndex = preprocessingContext.allLabels.firstPhraseIndex;
        final int [] labelsFeatureIndex = preprocessingContext.allLabels.featureIndex;
        final int [] mostFrequentOriginalWordIndex = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        final int desiredClusterCount = stemCos.columns();

        final IntArrayList clusterLabelFeatureIndex = new IntArrayList(
            desiredClusterCount);
        final DoubleArrayList clusterLabelScore = new DoubleArrayList(desiredClusterCount);
        for (int label = 0; label < desiredClusterCount; label++)
        {
            final Pair<Integer, Integer> stemMax = max(stemCos);
            final Pair<Integer, Integer> phraseMax = max(phraseCos);

            if (stemMax == null && phraseMax == null)
            {
                break;
            }

            double stemScore = stemMax != null ? stemCos.getQuick(stemMax.objectA,
                stemMax.objectB) : -1;
            double phraseScore = phraseMax != null ? phraseCos.getQuick(
                phraseMax.objectA, phraseMax.objectB) : -1;

            if (phraseScore > stemScore)
            {
                phraseCos.viewRow(phraseMax.objectA).assign(0);
                phraseCos.viewColumn(phraseMax.objectB).assign(0);
                stemCos.viewColumn(phraseMax.objectB).assign(0);

                clusterLabelFeatureIndex.add(labelsFeatureIndex[phraseMax.objectA
                    + firstPhraseIndex]);
                clusterLabelScore.add(phraseScore);
            }
            else
            {
                stemCos.viewRow(stemMax.objectA).assign(0);
                stemCos.viewColumn(stemMax.objectB).assign(0);
                if (phraseCos != null)
                {
                    phraseCos.viewColumn(stemMax.objectB).assign(0);
                }

                clusterLabelFeatureIndex
                    .add(mostFrequentOriginalWordIndex[filteredRowToStemIndex
                        .get(stemMax.objectA)]);
                clusterLabelScore.add(stemScore);
            }
        }

        context.clusterLabelFeatureIndex = clusterLabelFeatureIndex.toArray();
        context.clusterLabelScore = clusterLabelScore.toArray();
    }

    private Pair<Integer, Integer> max(DoubleMatrix2D matrix)
    {
        if (matrix == null)
        {
            return null;
        }

        int row = 0;
        int column = 0;
        double value = 0;

        for (int r = 0; r < matrix.rows(); r++)
        {
            for (int c = 0; c < matrix.columns(); c++)
            {
                final double currentValue = matrix.getQuick(r, c);
                if (currentValue > value)
                {
                    value = currentValue;
                    row = r;
                    column = c;
                }
            }
        }

        if (value > 0)
        {
            return new Pair<Integer, Integer>(row, column);
        }
        else
        {
            return null;
        }
    }
}
