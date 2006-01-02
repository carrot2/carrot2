
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
public class JaccardSimilarity
    implements SimilarityMeasure
{
    public float [][] calculateSimilarity(float [][] termsWeights)
    {
        int docsCount = termsWeights.length;
        float [][] similarities = new float[docsCount][docsCount];
        float numerator;
        float denominator;
        float sumX;
        float sumY;

        for (int i = 0; i < docsCount; i++)
        {
            // calculating of similarities of the i-th document and
            // j-th documents, j = i..n
            similarities[i][i] = 1;

            for (int j = i + 1; j < docsCount; j++)
            {
                // calculating of similarities of the i-th document and
                // j-th document
                numerator = 0;
                sumX = 0;
                sumY = 0;

                for (int term = 0; term < termsWeights[i].length; term++)
                {
                    numerator += (termsWeights[i][term] * termsWeights[j][term]);

                    sumX += (termsWeights[i][term] * termsWeights[i][term]);
                    sumY += (termsWeights[j][term] * termsWeights[j][term]);
                }

                denominator = (sumX + sumY) - numerator;

                if (denominator != 0)
                {
                    similarities[i][j] = similarities[j][i] = numerator / denominator;
                }
                else
                {
                    // both given documents don't contain any terms
                    // -> similarity = 0
                    similarities[i][j] = similarities[j][i] = 0;
                }
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
