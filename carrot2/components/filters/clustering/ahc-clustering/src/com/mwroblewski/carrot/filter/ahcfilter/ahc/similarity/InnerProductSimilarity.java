

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity;


/**
 * @author Micha³ Wróblewski
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
