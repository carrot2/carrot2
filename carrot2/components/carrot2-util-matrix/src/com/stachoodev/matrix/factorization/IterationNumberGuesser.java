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
package com.stachoodev.matrix.factorization;

import java.util.*;

import com.stachoodev.matrix.factorization.seeding.*;

import cern.colt.matrix.*;

/**
 * This class helps to guesstimate the number of iterations for iterative
 * factorization algorithms. Note: for the time being it uses a very simple
 * linear model with lots of dodgy assumptions.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class IterationNumberGuesser
{
    /** Coefficients for all known combinations of input parameters */
    private static Map allKnownCoefficients;

    static
    {
        allKnownCoefficients = new HashMap();

        /** NMF-ED, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(1) }), new double []
        { -0.0166, 0.3333, 8.0000 });

        /** NMF-ED, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(2) }), new double []
        { -0.0175, 0.6, 12.0 });

        /** NMF-ED, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(3) }), new double []
        { -0.0186, 0.8222, 17.3555 });

        // TODO: for the time being the values have been just C&P'd from NMF-ED
        /** NMF-KL, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationKLFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(1) }), new double []
        { -0.0166, 0.3333, 8.0000 });

        /** NMF-KL, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationKLFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(2) }), new double []
        { -0.0175, 0.6, 12.0 });

        /** NMF-KL, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationKLFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(3) }), new double []
        { -0.0186, 0.8222, 17.3555 });

        /** NMF-ED, KMeans seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         KMeansSeedingStrategyFactory.class, new Integer(1) }), new double []
        { -0.005, 0, 6 });

        /** NMF-ED, KMeans seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         KMeansSeedingStrategyFactory.class, new Integer(2) }), new double []
        { -0.005, 0, 10 });

        /** NMF-ED, KMeans seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { NonnegativeMatrixFactorizationEDFactory.class,
         KMeansSeedingStrategyFactory.class, new Integer(3) }), new double []
        { -0.005, 0, 20 });

        /** LNMF, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { LocalNonnegativeMatrixFactorizationFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(1) }), new double []
        { -0.014, 0.1, 7.5 });

        /** LNMF, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { LocalNonnegativeMatrixFactorizationFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(2) }), new double []
        { -0.0175, 0.15, 11.3333 });

        /** LNMF, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        { LocalNonnegativeMatrixFactorizationFactory.class,
         RandomSeedingStrategyFactory.class, new Integer(3) }), new double []
        { -0.02333, 0.2, 16.8888 });
    }

    /**
     * Sets the guesstimated iterations number in the <code>factory</code>.
     * Different models are used depending on the actual factorization
     * algorithm, the seeding strategy and the <code>qualityLevel</code>,
     * which can be 1 (low), 2 (medium) and 3 (high, supposedly :). For the time
     * being a crude linear model is assumed based on:
     * 
     * <ul>
     * <li>the number of columns in <code>A</code>. The number of rows is
     * assumed to be 2.8 bigger than the number of rows (2.8 is pretty much
     * random, the value is taken from an application to search results
     * clustering). In case matrix has different proportions, the size will be
     * scaled so that the cardinality of the matrix (total number of elements)
     * is maintained. The model supports column counts (after scaling) ranging
     * from 50 to 400.
     * <li>the number of requested base vectors (read from the factorization
     * factory). The model supports k = 5 ... 40.
     * </ul>
     * 
     * A new maximum number of iterations will be set in the factory <b>only
     * </b> if the requested parameters fall within the ranges supported by the
     * model. In this case <code>true</code> will be returned.
     * 
     * @param factory
     * @param A
     * @param qualityLevel
     * @return
     */
    public static boolean setEstimatedIterationsNumber(
        IterativeMatrixFactorizationFactory factory, DoubleMatrix2D A,
        int qualityLevel)
    {
        double [] coefficients = (double []) allKnownCoefficients.get(Arrays
            .asList(new Object []
            { factory.getClass(), factory.getSeedingFactory().getClass(),
             new Integer(qualityLevel) }));

        if (coefficients != null)
        {
            double columns = Math.sqrt(A.rows() * A.columns() / 2.8);
            if (columns < 50 || columns > 400 || factory.getK() < 5
                || factory.getK() > 50)
            {
                // That's probably beyond our simplistic model
                return false;
            }
            else
            {
                int iterations = (int) (columns * coefficients[0]
                    + factory.getK() * coefficients[1] + coefficients[2]);
                factory.setMaxIterations(iterations);

                return true;
            }
        }
        else
        {
            return false;
        }
    }
}