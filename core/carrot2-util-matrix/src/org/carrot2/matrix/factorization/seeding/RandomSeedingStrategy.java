
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization.seeding;

import java.util.Random;

import org.apache.mahout.math.function.UnaryFunction;
import org.apache.mahout.math.matrix.*;

/**
 * Random matrix factorization seeding.
 */
@SuppressWarnings("deprecation")
public class RandomSeedingStrategy implements ISeedingStrategy
{
    /** Colt's random number generator */
    private UnaryFunction random;

    /**
     * Creates RandomSeedingStrategy with seed based on current time.
     */
    public RandomSeedingStrategy()
    {
        random = new RandomDoubleFunction(new Random());
    }

    /**
     * Creates RandomSeedingStrategy with given random seed.
     * 
     * @param seed
     */
    public RandomSeedingStrategy(int seed)
    {
        random = new RandomDoubleFunction(new Random(seed));
    }

    public void seed(DoubleMatrix2D A, DoubleMatrix2D U, DoubleMatrix2D V)
    {
        U.assign(random);
        V.assign(random);
    }

    public String toString()
    {
        return "R";
    }

    /**
     * Internal Colt function for generating random values.
     */
    static class RandomDoubleFunction implements UnaryFunction
    {
        final Random random;

        RandomDoubleFunction(Random random)
        {
            this.random = random;
        }

        public double apply(double arg)
        {
            return random.nextDouble();
        }
    }
}
