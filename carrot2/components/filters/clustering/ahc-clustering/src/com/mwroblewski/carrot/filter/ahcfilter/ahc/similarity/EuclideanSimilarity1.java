

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
public class EuclideanSimilarity1
    implements SimilarityMeasure
{
    public float [][] calculateSimilarity(float [][] termsWeights)
    {
        int docsCount = termsWeights.length;
        float [][] similarities = new float[docsCount][docsCount];
        float distance;
        float distance1D;
        float distanceMax = minSimilarity();

        for (int i = 0; i < docsCount; i++)
        {
            // calculating of similarities of the i-th document and
            // j-th documents, j = i..n
            similarities[i][i] = 1.0f;

            for (int j = i + 1; j < docsCount; j++)
            {
                // calculating of similarities of the i-th document and
                // j-th document
                distance = 0.0f;

                // first - calculating the distance
                for (int term = 0; term < termsWeights[i].length; term++)
                {
                    distance1D = termsWeights[i][term] - termsWeights[j][term];
                    distance += (distance1D * distance1D);
                }

                similarities[i][j] = distance = (float) Math.sqrt(distance);

                if (distance > distanceMax)
                {
                    distanceMax = distance;
                }
            }
        }

        for (int i = 0; i < docsCount; i++)
        {
            for (int j = i + 1; j < docsCount; j++)
            {
                similarities[i][j] = similarities[j][i] = 1.0f - (similarities[i][j] / distanceMax);
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
