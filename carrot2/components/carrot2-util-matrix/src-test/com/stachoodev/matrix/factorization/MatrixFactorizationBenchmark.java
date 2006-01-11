
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

package com.stachoodev.matrix.factorization;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.util.common.*;

import cern.colt.matrix.*;
import cern.colt.matrix.impl.*;

/**
 * Benchmarks the matrix factorisation algorithms.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class MatrixFactorizationBenchmark
{
    /** Used to calculate the number of rows of the test matrix */
    private static final double ROWS_COLUMNS_RATIO = 2.8;

    /** Used to calculate k based on the number of columns */
    private static final double COLUMNS_K_RATIO = 4;

    /** Time loging utility */
    private ElapsedTimeTimer timer;

    /** Iterative matrix factorizations under tests */
    private IterativeMatrixFactorizationFactory [] factories = new IterativeMatrixFactorizationFactory []
    { //new KMeansMatrixFactorizationFactory(),
     new NonnegativeMatrixFactorizationEDFactory(),
     //new NonnegativeMatrixFactorizationKLFactory(),
     new LocalNonnegativeMatrixFactorizationFactory(), };

    /** Where to write the results */
    private PrintStream resultsPrintStream = System.out;

    /**
     *  
     */
    public MatrixFactorizationBenchmark()
    {
        timer = new ElapsedTimeTimer();
    }

    /**
     *  
     */
    public void benchmarkExecutionTime()
    {
        int [] columnCounts = new int []
        { 50, 100, 150, 200, 300, 400 };

        // Output header
        resultsPrintStream
            .println("Algorithm\tMode\tTerms\tDocuments\tClusters\tIterations\tTime");

        for (int i = 0; i < factories.length; i++)
        {
            IterativeMatrixFactorizationFactory factory = factories[i];

            // Set paremeters
            factory.setMaxIterations(10);
            factory.setStopThreshold(-1);

            DoubleMatrix2D A;
            MatrixFactorization factorization;
            long elapsed;

            for (int j = 0; j < columnCounts.length; j++)
            {
                A = createRandomTdMatrix(
                    (int) (columnCounts[j] * ROWS_COLUMNS_RATIO),
                    columnCounts[j]);
                factory.setK((int) (A.columns() / COLUMNS_K_RATIO));

                // Execute Java
                NNIInterface.suppressNNI(true);
                timer.restart();
                factorization = factory.factorize(A);
                elapsed = timer.elapsed();

                // Output report
                resultsPrintStream.println(factorization.toString() + "\t"
                    + "Java" + "\t" + A.rows() + "\t" + A.columns() + "\t"
                    + factory.getK() + "\t" + factory.getMaxIterations() + "\t"
                    + elapsed);

                // Execute NNI if available
                NNIInterface.suppressNNI(false);
                if (NNIInterface.isNativeBlasAvailable())
                {
                    timer.restart();
                    factorization = factory.factorize(A);
                    elapsed = timer.elapsed();

                    // Output report
                    resultsPrintStream.println(factorization.toString() + "\t"
                        + "NNI" + "\t" + A.rows() + "\t" + A.columns() + "\t"
                        + factory.getK() + "\t" + factory.getMaxIterations()
                        + "\t" + elapsed);
                }
            }
        }
    }

    /**
     * @param t
     * @param d
     */
    private DoubleMatrix2D createRandomTdMatrix(int t, int d)
    {
        DoubleMatrix2D matrix = NNIDoubleFactory2D.nni.make(t, d);
        Random random = new Random(0);
        double termsPerDocument = 0.01 + 0.01 * random.nextDouble();

        for (int i = 0; i < d; i++)
        {
            for (int j = 0; j < t * termsPerDocument; j++)
            {
                matrix.setQuick(random.nextInt(t), i, random.nextDouble());
            }
        }

        return matrix;
    }

    /**
     * @param params
     */
    public static void main(String [] params)
    {
        MatrixFactorizationBenchmark benchmark = new MatrixFactorizationBenchmark();
        benchmark.benchmarkExecutionTime();
    }
}