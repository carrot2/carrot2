
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization.seeding;

import com.stachoodev.colt.function.*;

import cern.colt.function.*;
import cern.colt.matrix.*;

/**
 * Random matrix factorization seeding.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RandomSeedingStrategy implements SeedingStrategy
{

    /** Colt's random number generator */
    private DoubleFunction random;

    /**
     * Creates RandomSeedingStrategy with seed based on current time.
     */
    public RandomSeedingStrategy()
    {
        random = new RandomDoubleFunction(new java.util.Date().getTime());
    }

    /**
     * Creates RandomSeedingStrategy with given random seed.
     * 
     * @param seed
     */
    public RandomSeedingStrategy(int seed)
    {
        random = new RandomDoubleFunction(seed);
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