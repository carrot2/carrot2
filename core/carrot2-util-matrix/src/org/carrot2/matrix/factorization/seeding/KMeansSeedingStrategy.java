
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

package org.carrot2.matrix.factorization.seeding;

import org.carrot2.mahout.math.matrix.*;
import org.carrot2.matrix.factorization.KMeansMatrixFactorization;

/**
 * Matrix seeding based on the k-means algorithms.
 */
public class KMeansSeedingStrategy implements ISeedingStrategy
{
    /** The maximum number of KMeans iterations */
    private int maxIterations;
    private static final int DEFAULT_MAX_ITERATIONS = 5;

    /**
     * Creates the KMeansSeedingStrategy.
     */
    public KMeansSeedingStrategy()
    {
        this(DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates the KMeansSeedingStrategy.
     * 
     * @param maxIterations maximum number of KMeans iterations.
     */
    public KMeansSeedingStrategy(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    public void seed(DoubleMatrix2D A, DoubleMatrix2D U, DoubleMatrix2D V)
    {
        KMeansMatrixFactorization kMeansMatrixFactorization = new KMeansMatrixFactorization(
                A);
        kMeansMatrixFactorization.setK(U.columns());
        kMeansMatrixFactorization.setMaxIterations(maxIterations);
        kMeansMatrixFactorization.compute();

        U.assign(kMeansMatrixFactorization.getU());
        for (int r = 0; r < U.rows(); r++)
        {
            for (int c = 0; c < U.columns(); c++)
            {
                if (U.getQuick(r, c) < 0.001)
                {
                    U.setQuick(r, c, 0.05);
                }
            }
        }

        V.assign(kMeansMatrixFactorization.getV());
        for (int r = 0; r < V.rows(); r++)
        {
            for (int c = 0; c < V.columns(); c++)
            {
                if (V.getQuick(r, c) == 0)
                {
                    V.setQuick(r, c, 0.05);
                }
            }
        }
    }
    
    public String toString()
    {
        return "KM";
    }
}
