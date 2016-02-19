
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

/**
 * Seeding based on a k-means clustering algorithm.
 */
public class KMeansSeedingStrategyFactory implements ISeedingStrategyFactory
{
    /** The default maximum number of iterations */
    private static int DEFAULT_MAX_ITERATIONS = 5;

    /**
     * The maximum number of iterations the k-means algorithm is allowed to
     * perform
     */
    private int maxIterations = DEFAULT_MAX_ITERATIONS;

    public ISeedingStrategy createSeedingStrategy()
    {
        KMeansSeedingStrategy seeding = new KMeansSeedingStrategy(maxIterations);

        return seeding;
    }

    /**
     * Returns the maximum number of iterations the k-means algorithm is allowed
     * to perform.
     * 
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations the k-means algorithm is allowed to
     * perform.
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    public String toString()
    {
        return "KM";
    }
}
