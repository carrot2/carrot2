/*
 * KMeansMatrixFactorization.java Created on 2004-05-11
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;
import cern.jet.math.*;

/**
 * @author stachoo
 */
public class KMeansMatrixFactorization extends
    IterativeMatrixFactorizationBase
{

    /**
     * Creates the KMeansMatrixFactorization object for matrix A. Before
     * accessing results, perform computations by calling the {@link #compute()}
     * method.
     * 
     * @param A matrix to be factorized. The matrix must have Euclidean
     *            length-normalised columns.
     */
    public KMeansMatrixFactorization(DoubleMatrix2D A)
    {
        super(A);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationBase#compute()
     */
    public void compute()
    {
        int n = A.columns();

        // Distances to centroids
        DoubleMatrix2D D = doubleFactory2D.make(k, n);

        // Object-cluster assignments
        V = doubleFactory2D.make(n, k);

        // Initialize the centroids with some document vectors
        U = A.viewPart(0, 0, A.rows(), k).copy();

        for (iterationsCompleted = 0; iterationsCompleted < maxIterations; iterationsCompleted++)
        {
            // Calculate cosine distances
            U.zMult(A, D, 1, 0, true, false);

            V.assign(0);
            U.assign(0);

            // For each object
            for (int d = 0; d < D.columns(); d++)
            {
                // Find the closest centroid
                int closestCentroid = 0;
                double min = D.getQuick(0, d);
                for (int c = 1; c < D.rows(); c++)
                {
                    if (D.getQuick(c, d) < min)
                    {
                        min = D.getQuick(c, d);
                        closestCentroid = c;
                    }
                }

                // Assign object to that centroid
                V.setQuick(d, closestCentroid, 1);
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
                        U.viewColumn(c).assign(A.viewColumn(d), Functions.plus);
                    }
                }

                // Divide
                if (count > 0)
                {
                    U.viewColumn(c).assign(Mult.div(count));
                }
                else
                {
                    // Assign a pseudo-random column
                    U.viewColumn(c).assign(
                        A.viewColumn(iterationsCompleted % A.columns()));
                }
            }

        }

        updateApproximationError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "KMMF";
    }
}