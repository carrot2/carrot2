/*
 * RandomSeedingStrategy.java Created on 2004-05-12
 */
package com.stachoodev.matrix.factorization.seeding;

import cern.colt.matrix.*;
import cern.jet.random.engine.*;

/**
 * Random matrix factorization seeding.
 * 
 * @author stachoo
 */
public class RandomSeedingStrategy implements SeedingStrategy
{

    /** Colt's random number generator */
    private DRand random;

    /**
     * Creates RandomSeedingStrategy with seed based on current time.
     */
    public RandomSeedingStrategy()
    {
        random = new DRand(new java.util.Date());
    }

    /**
     * Creates RandomSeedingStrategy with given random seed.
     * 
     * @param seed
     */
    public RandomSeedingStrategy(int seed)
    {
        random = new DRand(seed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.SeedingStrategy#seed(cern.colt.matrix.DoubleMatrix2D,
     *      cern.colt.matrix.DoubleMatrix2D)
     */
    public void seed(DoubleMatrix2D A, DoubleMatrix2D U, DoubleMatrix2D V)
    {
        U.assign(random);
        V.assign(random);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "R";
    }
}