/*
 * KMeansSeedingStrategyFactory.java Created on 2004-06-20
 */
package com.stachoodev.matrix.factorization.seeding;

/**
 * Seeding based on a k-means clustering algorithm.
 * @author stachoo
 */
public class KMeansSeedingStrategyFactory implements SeedingStrategyFactory
{
    /** The default maximum number of iterations */
    private static int DEFAULT_MAX_ITERATIONS = 20;

    /**
     * The maximum number of iterations the k-means algorithm is allowed to
     * perform
     */
    private int maxIterations = DEFAULT_MAX_ITERATIONS;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.seeding.SeedingStrategyFactory#createSeedingStrategy()
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
     * @return
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
}