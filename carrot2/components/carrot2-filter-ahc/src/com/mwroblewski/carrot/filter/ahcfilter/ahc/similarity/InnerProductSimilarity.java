
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity;


/**
 * @author Micha� Wr�blewski
 */
public class InnerProductSimilarity
    implements SimilarityMeasure
{
    public float [][] calculateSimilarity(float [][] termsWeights)
    {
        int docsCount = termsWeights.length;
        float [][] similarities = new float[docsCount][docsCount];
        float similarity;

        for (int i = 0; i < docsCount; i++)
        {
            // calculating of similarities of the i-th document and
            // j-th documents, j = i..n
            similarities[i][i] = 1;

            for (int j = i + 1; j < docsCount; j++)
            {
                // calculating of similarities of the i-th document and
                // j-th document
                similarity = 0;

                for (int term = 0; term < termsWeights[i].length; term++)
                {
                    similarity += (termsWeights[i][term] * termsWeights[j][term]);
                }

                similarities[i][j] = similarity;
                similarities[j][i] = similarity;
            }
        }

        return similarities;
    }


    public float maxSimilarity()
    {
        return Float.MAX_VALUE;
    }


    public float minSimilarity()
    {
        return 0.0f;
    }
}
