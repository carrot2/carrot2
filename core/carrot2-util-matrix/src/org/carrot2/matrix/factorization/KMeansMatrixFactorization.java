
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

import org.carrot2.mahout.math.function.Functions;
import org.carrot2.mahout.math.function.Mult;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.matrix.MatrixUtils;

/**
 * Performs matrix factorization using the K-means clustering algorithm. This kind of
 * factorization is sometimes referred to as Concept Decomposition Factorization.
 */
public class KMeansMatrixFactorization extends IterativeMatrixFactorizationBase
{
    /**
     * Creates the KMeansMatrixFactorization object for matrix A. Before accessing
     * results, perform computations by calling the {@link #compute()} method.
     * 
     * @param A matrix to be factorized. The matrix must have Euclidean length-normalized
     *            columns.
     */
    public KMeansMatrixFactorization(DoubleMatrix2D A)
    {
        super(A);
    }

    public void compute()
    {
        int n = A.columns();

        // Distances to centroids
        DoubleMatrix2D D = new DenseDoubleMatrix2D(k, n);

        // Object-cluster assignments
        V = new DenseDoubleMatrix2D(n, k);

        // Initialize the centroids with some document vectors
        U = new DenseDoubleMatrix2D(A.rows(), k);
        U.assign(A.viewPart(0, 0, A.rows(), k));

        int [] minIndices = new int [D.columns()];
        double [] minValues = new double [D.columns()];

        for (iterationsCompleted = 0; iterationsCompleted < maxIterations; iterationsCompleted++)
        {
            // Calculate cosine distances
            U.zMult(A, D, 1, 0, true, false);

            V.assign(0);
            U.assign(0);

            // For each object
            MatrixUtils.maxInColumns(D, minIndices, minValues);
            for (int i = 0; i < minIndices.length; i++)
            {
                V.setQuick(i, minIndices[i], 1);
            }

            // Update centroids
            for (int c = 0; c < V.columns(); c++)
            {
                // Sum
                int count = 0;
                for (int d = 0; d < V.rows(); d++)
                {
                    if (V.getQuick(d, c) != 0)
                    {
                        count++;
                        U.viewColumn(c).assign(A.viewColumn(d), Functions.PLUS);
                    }
                }

                // Divide
                U.viewColumn(c).assign(Mult.div(count));
                MatrixUtils.normalizeColumnL2(U, null);
            }

        }
    }

    public String toString()
    {
        return "KMMF";
    }
}
