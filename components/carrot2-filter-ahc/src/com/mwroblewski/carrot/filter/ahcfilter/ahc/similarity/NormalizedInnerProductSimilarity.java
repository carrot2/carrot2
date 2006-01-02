
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity;


/**
 * @author Micha� Wr�blewski
 */
public class NormalizedInnerProductSimilarity
    implements SimilarityMeasure
{
    public float [][] calculateSimilarity(float [][] termsWeights)
    {
        int docsCount = termsWeights.length;
        float [][] similarities = new float[docsCount][docsCount];
        float similarity;
        float maxSimilarity = 0.0f;

        for (int i = 0; i < docsCount; i++)
        {
            // calculating of similarities of the i-th document and
            // j-th documents, j = i..n
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

                if (similarity > maxSimilarity)
                {
                    maxSimilarity = similarity;
                }
            }
        }

        for (int i = 0; i < docsCount; i++)
        {
            similarities[i][i] = 1.0f;

            for (int j = i + 1; j < docsCount; j++)
            {
                similarities[i][j] = similarities[i][j] / maxSimilarity;
                similarities[j][i] = similarities[i][j];
            }
        }

        return similarities;
    }


    public float maxSimilarity()
    {
        return 1.0f;
    }


    public float minSimilarity()
    {
        return 0.0f;
    }
}
