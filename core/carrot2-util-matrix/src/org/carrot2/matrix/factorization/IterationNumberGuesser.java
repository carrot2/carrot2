
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

package org.carrot2.matrix.factorization;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.carrot2.mahout.math.matrix.*;
import org.carrot2.matrix.factorization.seeding.KMeansSeedingStrategyFactory;
import org.carrot2.matrix.factorization.seeding.RandomSeedingStrategyFactory;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * This class helps to guesstimate the number of iterations for iterative factorization
 * algorithms. Note: for the time being it uses a very simple linear model with lots of
 * dodgy assumptions.
 */
public class IterationNumberGuesser
{
    /**
     * Factorization quality.
     */
    public enum FactorizationQuality
    {
        /** Low, quick processing */
        LOW,

        /** Medium, longer processing */
        MEDIUM,

        /** High, long processing */
        HIGH;

        @Override
        public String toString()
        {
            return StringUtils.capitalize(name().toLowerCase());
        }
    }

    /** Coefficients for all known combinations of input parameters */
    private static Map<List<Object>, double []> allKnownCoefficients;

    static
    {
        allKnownCoefficients = Maps.newHashMap();

        /** NMF-ED, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.LOW
        }), new double []
        {
            -0.0166, 0.3333, 8.0000
        });

        /** NMF-ED, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.MEDIUM
        }), new double []
        {
            -0.0175, 0.6, 12.0
        });

        /** NMF-ED, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.HIGH
        }), new double []
        {
            -0.0186, 0.8222, 17.3555
        });

        /** NMF-KL, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.LOW
        }), new double []
        {
            -0.0166, 0.3333, 8.0000
        });

        /** NMF-KL, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.MEDIUM
        }), new double []
        {
            -0.0175, 0.6, 12.0
        });

        /** NMF-KL, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationKLFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.HIGH
        }), new double []
        {
            -0.0186, 0.8222, 17.3555
        });

        /** NMF-ED, KMeans seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class, FactorizationQuality.LOW
        }), new double []
        {
            -0.005, 0, 6
        });

        /** NMF-ED, KMeans seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class, FactorizationQuality.MEDIUM
        }), new double []
        {
            -0.005, 0, 10
        });

        /** NMF-ED, KMeans seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            NonnegativeMatrixFactorizationEDFactory.class,
            KMeansSeedingStrategyFactory.class, FactorizationQuality.HIGH
        }), new double []
        {
            -0.005, 0, 20
        });

        /** LNMF, Random seeding, level 1 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.LOW
        }), new double []
        {
            -0.014, 0.1, 7.5
        });

        /** LNMF, Random seeding, level 2 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.MEDIUM
        }), new double []
        {
            -0.0175, 0.15, 11.3333
        });

        /** LNMF, Random seeding, level 3 */
        allKnownCoefficients.put(Arrays.asList(new Object []
        {
            LocalNonnegativeMatrixFactorizationFactory.class,
            RandomSeedingStrategyFactory.class, FactorizationQuality.HIGH
        }), new double []
        {
            -0.02333, 0.2, 16.8888
        });
    }

    /**
     * Sets the guesstimated iterations number in the <code>factory</code>. Different
     * models are used depending on the actual factorization algorithm, the seeding
     * strategy and the <code>qualityLevel</code>. For the time being a crude linear model
     * is assumed based on:
     * <ul>
     * <li>the number of columns in <code>A</code>. The number of rows is assumed to be
     * 2.8 bigger than the number of rows (2.8 is pretty much random, the value is taken
     * from an application to search results clustering). In case matrix has different
     * proportions, the size will be scaled so that the cardinality of the matrix (total
     * number of elements) is maintained. The model supports column counts (after scaling)
     * ranging from 50 to 400.
     * <li>the number of requested base vectors (read from the factorization factory). The
     * model supports k = 5 ... 50.
     * </ul>
     * A new maximum number of iterations will be set in the factory <b>only </b> if the
     * requested parameters fall within the ranges supported by the model. In this case
     * <code>true</code> will be returned.
     */
    public static boolean setEstimatedIterationsNumber(
        IterativeMatrixFactorizationFactory factory, DoubleMatrix2D A,
        FactorizationQuality qualityLevel)
    {
        double [] coefficients = allKnownCoefficients.get(Arrays.asList(new Object []
        {
            factory.getClass(), factory.getSeedingFactory().getClass(), qualityLevel
        }));

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
                int iterations = (int) (columns * coefficients[0] + factory.getK()
                    * coefficients[1] + coefficients[2]);
                factory.setMaxIterations((int) (iterations * 0.6));

                return true;
            }
        }
        else
        {
            return false;
        }
    }
}
