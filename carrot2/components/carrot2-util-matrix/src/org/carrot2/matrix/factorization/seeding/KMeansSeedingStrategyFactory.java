
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization.seeding;

/**
 * Seeding based on a k-means clustering algorithm.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class KMeansSeedingStrategyFactory implements SeedingStrategyFactory
{
    /** The default maximum number of iterations */
    private static int DEFAULT_MAX_ITERATIONS = 5;

    /**
     * The maximum number of iterations the k-means algorithm is allowed to
     * perform
     */
    private int maxIterations = DEFAULT_MAX_ITERATIONS;

    /*
     * (non-Javadoc)
     */
    public SeedingStrategy createSeedingStrategy()
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
     * 
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "KM";
    }
}