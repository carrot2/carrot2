/*
 * KMeansSeedingStrategy.java
 * 
 * Created on 2004-05-12
 */
package com.stachoodev.matrix.factorization.seeding;

import com.stachoodev.matrix.factorization.*;
import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class KMeansSeedingStrategy implements SeedingStrategy
{
    /** The maximum number of KMeans iterations */
    private int maxIterations;
    private static final int DEFAULT_MAX_ITERATIONS = 10;

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