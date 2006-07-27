
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

package org.carrot2.matrix.factorization;

import org.carrot2.matrix.MatrixUtils;

import com.stachoodev.matrix.*;

import cern.colt.matrix.*;
import cern.jet.math.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
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
        U = doubleFactory2D.make(A.rows(), k);
        U.assign(A.viewPart(0, 0, A.rows(), k));

        int [] minIndices = new int[D.columns()];
        double [] minValues = new double[D.columns()];
        
        for (iterationsCompleted = 0; iterationsCompleted < maxIterations; iterationsCompleted++)
        {
            // Calculate cosine distances
            U.zMult(A, D, 1, 0, true, false);

            V.assign(0);
            U.assign(0);

            // For each object
            MatrixUtils.minInColumns(D, minIndices, minValues);
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