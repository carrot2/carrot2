/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix.factorization.seeding;

import com.stachoodev.matrix.factorization.*;
import cern.colt.matrix.*;

/**
 * Matrix seeding based on the k-means algorithms.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class KMeansSeedingStrategy implements SeedingStrategy
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

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.SeedingStrategy#seed(cern.colt.matrix.DoubleMatrix2D,
     *      cern.colt.matrix.DoubleMatrix2D)
     */
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
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "KM";
    }
}