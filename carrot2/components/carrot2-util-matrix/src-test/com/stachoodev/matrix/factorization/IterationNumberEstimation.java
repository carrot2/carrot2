
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix.factorization;

import java.io.*;
import java.util.*;

import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;

import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.matrix.*;
import com.stachoodev.matrix.factorization.seeding.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class IterationNumberEstimation
{
    /** Used to calculate the number of rows of the test matrix */
    private static final double ROWS_COLUMNS_RATIO = 2.8;

    /** */
    private int [] ks =
    { 5, 10, 15, 20, 25, 30, 40, 50 };

    /** */
    private int [] sizes =
    { 100, 150, 200, 250, 300, 400};

    /** */
    private int maxIterations = 100;

    /** */
    private int matrices = 10;

    /** */
    private SeedingStrategyFactory [] seedings = new SeedingStrategyFactory []
    { new RandomSeedingStrategyFactory(), new KMeansSeedingStrategyFactory() };

    /** */
    private IterativeMatrixFactorizationFactory [] factories = new IterativeMatrixFactorizationFactory []
    { new NonnegativeMatrixFactorizationEDFactory(),
     new LocalNonnegativeMatrixFactorizationFactory() };

    /** */
    private double [] diffs = new double []
    { 1.2, 1.1, 1.05, 1.01, 1.005, 1.001, 1.0005, 1.0001 };

    /** */
    private static final double SPARSENESS = 0.015;

    /** */
    private static final double SPARSENESS_DEV = 0.01;

    /** Where to write the results */
    private PrintStream resultsPrintStream;

    /** */
    private static final double MIN_ERROR_DIFF = 0.00005;

    /**
     *  
     */
    public IterationNumberEstimation()
    {
        this(System.out);
    }

    /**
     * @param printStream
     */
    public IterationNumberEstimation(PrintStream printStream)
    {
        resultsPrintStream = printStream;
    }

    /**
     *  
     */
    public void calculate()
    {
        resultsPrintStream
            .println("Size\tMatrix\tF-Norm\tNonZero\tSparseness\tAlgorithm\tSeeding\tK\tIteration\tError");
        for (int s = 0; s < sizes.length; s++)
        {
            System.out.print("\nSize: " + sizes[s] + " ");
            DoubleMatrix2D A = NNIDoubleFactory2D.nni.make(
                (int) (sizes[s] * ROWS_COLUMNS_RATIO), sizes[s]);
            for (int m = 0; m < matrices; m++)
            {
                System.out.print(m + " ");
                System.out.flush();

                A.assign(0);
                fill(A, SPARSENESS, SPARSENESS_DEV);
                double fNorm = Algebra.DEFAULT.normF(A);
                int nonZero = A.cardinality();
                double sparseness = MatrixUtils.computeSparseness(A);

                for (int f = 0; f < factories.length; f++)
                {
                    IterativeMatrixFactorizationFactory factory = factories[f];
                    factory.setMaxIterations(maxIterations);
                    factory.setStopThreshold(MIN_ERROR_DIFF);

                    for (int e = 0; e < seedings.length; e++)
                    {
                        SeedingStrategyFactory seeding = seedings[e];
                        factory.setSeedingFactory(seeding);

                        for (int ki = 0; ki < ks.length; ki++)
                        {
                            factory.setK(ks[ki]);
                            IterativeMatrixFactorizationBase factorization = (IterativeMatrixFactorizationBase) factory
                                .factorize(A);

                            double [] errors = factorization
                                .getApproximationErrors();
                            int i = 1;
                            for (int d = 0; d < diffs.length; d++)
                            {
                                double error = errors[i]
                                    / factorization.getApproximationError();
                                while (error > diffs[d])
                                {
                                    error = errors[i]
                                        / factorization.getApproximationError();
                                    i++;
                                }

                                resultsPrintStream.println(sizes[s]
                                    + "\t"
                                    + m
                                    + "\t"
                                    + StringUtils.toString(new Double(fNorm),
                                        "#.###")
                                    + "\t"
                                    + nonZero
                                    + "\t"
                                    + StringUtils.toString(new Double(
                                        sparseness), "#.###")
                                    + "\t"
                                    + factorization.toString()
                                    + "\t"
                                    + seeding.toString()
                                    + "\t"
                                    + ks[ki]
                                    + "\t"
                                    + i
                                    + "\t"
                                    + StringUtils.toString(new Double(diffs[d]),
                                        "#.####"));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param t
     * @param d
     * @return
     */
    private void fill(DoubleMatrix2D A, double sparseness, double sparsenessDev)
    {
        Random random = new Random();
        double termsPerDocument = sparseness + sparsenessDev
            * random.nextDouble();

        for (int c = 0; c < A.columns(); c++)
        {
            for (int r = 0; r < termsPerDocument * A.rows(); r++)
            {
                A.setQuick(random.nextInt(A.rows()), c, random.nextDouble());
            }
        }

        MatrixUtils.normaliseColumnL2(A, null);
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String [] args) throws IOException
    {
        FileOutputStream out = new FileOutputStream(args[0]);
        IterationNumberEstimation iterationNumberEstimation = new IterationNumberEstimation(
            new PrintStream(out));
        iterationNumberEstimation.calculate();
        out.close();
    }
}