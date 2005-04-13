/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.matrix;

import java.io.*;
import java.text.*;
import java.util.*;

import cern.colt.matrix.*;
import cern.colt.matrix.impl.*;

import com.stachoodev.matrix.factorization.*;

/**
 * Quickly checks if the native NNI libraries can be loaded and what the
 * performance gain is.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class NNIQuickCheck
{
    /** Used to calculate the number of rows of the test matrix */
    private static final double ROWS_COLUMNS_RATIO = 2.8;

    /** Used to calculate k based on the number of columns */
    private static final double COLUMNS_K_RATIO = 4;

    /** Where to write the results */
    private PrintStream resultsPrintStream = System.out;

    /**
     * 
     */
    public NNIQuickCheck()
    {
    }
    
    /**
     * 
     */
    public void go()
    {
        NNIInterface.suppressNNI(false);
        if (NNIInterface.isNativeBlasAvailable() && NNIInterface.isNativeLapackAvailable())
        {
            resultsPrintStream.println("NNI native libraries available.");
            resultsPrintStream.println("Warming up...");
            benchmarkExecutionTime(true);
            resultsPrintStream.println("Benchmarking...");
            benchmarkExecutionTime(false);
        }   
        else
        {
            resultsPrintStream.println("NNI native libraries unavailable.");
        }
        resultsPrintStream.println("Done.");
    }
    
    /**
     *  
     */
    public void benchmarkExecutionTime(boolean warmUp)
    {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        
        int [] columnCounts = new int []
        { 100, 150, 200, 300, 400, 500};
        
        long start = 0;
        long stop = 0;
        double minSpeedUp = 10e10;
        double maxSpeedUp = 0;
        double avgSpeedUp = 0;

        // Output header
        if (!warmUp)
        {
            resultsPrintStream
                .println("Algorithm\tMode\tTerms\tDocuments\tClusters\tIterations\tTime");
        }

        IterativeMatrixFactorizationFactory factory = new NonnegativeMatrixFactorizationEDFactory();

        // Set paremeters
        factory.setMaxIterations(10);
        factory.setStopThreshold(-1);

        DoubleMatrix2D A;
        MatrixFactorization factorization;
        long elapsed;
        long elapsedNative;

        for (int j = 0; j < columnCounts.length; j++)
        {
            A = createRandomTdMatrix(
                (int) (columnCounts[j] * ROWS_COLUMNS_RATIO),
                columnCounts[j]);
            factory.setK((int) (A.columns() / COLUMNS_K_RATIO));

            // Execute Java
            NNIInterface.suppressNNI(true);
            start = System.currentTimeMillis();
            factorization = factory.factorize(A);
            stop = System.currentTimeMillis();
            elapsed = stop - start;

            // Output report
            if (!warmUp)
            {
                resultsPrintStream.println(factorization.toString() + "\t"
                    + "Java" + "\t" + A.rows() + "\t" + A.columns() + "\t"
                    + factory.getK() + "\t" + factory.getMaxIterations() + "\t"
                    + elapsed);
            }

            // Execute NNI
            NNIInterface.suppressNNI(false);
            start = System.currentTimeMillis();
            factorization = factory.factorize(A);
            stop = System.currentTimeMillis();
            elapsedNative = stop - start;

            // Output report
            if (!warmUp)
            {
                resultsPrintStream.println(factorization.toString() + "\t"
                    + "NNI" + "\t" + A.rows() + "\t" + A.columns() + "\t"
                    + factory.getK() + "\t" + factory.getMaxIterations()
                    + "\t" + elapsedNative);
                
                double speedUp = elapsed/(double)elapsedNative;
                if (minSpeedUp > speedUp)
                {
                    minSpeedUp = speedUp;
                }
                if (maxSpeedUp < speedUp)
                {
                    maxSpeedUp = speedUp;
                }
                avgSpeedUp += speedUp;
            }
        }
        
        if (!warmUp)
        {
            avgSpeedUp /= columnCounts.length;
            
            resultsPrintStream.println("Min speedup: " + format.format(minSpeedUp));
            resultsPrintStream.println("Max speedup: " + format.format(maxSpeedUp));
            resultsPrintStream.println("Avg speedup: " + format.format(avgSpeedUp));
        }
    }
    
    /**
     * @param t
     * @param d
     * @return
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
     * @param args
     */
    public static void main(String [] args)
    {
        new NNIQuickCheck().go();
    }
}
