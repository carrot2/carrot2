package org.carrot2.matrix.factorization.seeding;

/**
 * Seeding based on a k-means clustering algorithm.
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

    public String toString()
    {
        return "KM";
    }
}