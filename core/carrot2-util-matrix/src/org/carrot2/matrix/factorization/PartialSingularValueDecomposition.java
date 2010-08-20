
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

package org.carrot2.matrix.factorization;

import java.util.Arrays;

import org.carrot2.matrix.*;

import org.apache.mahout.math.matrix.*;
import org.apache.mahout.math.matrix.impl.*;
import org.apache.mahout.math.matrix.linalg.SingularValueDecomposition;

/**
 * Performs matrix factorization using the Singular Value Decomposition algorithm.
 */
@SuppressWarnings("deprecation")
public class PartialSingularValueDecomposition extends MatrixFactorizationBase implements
    IMatrixFactorization
{
    /** The desired number of base vectors */
    protected int k;

    /** The default number of desired base vectors */
    protected static final int DEFAULT_K = -1;

    /**
     * Work array
     */
    private double [] work;

    /**
     * Work array
     */
    private int [] iwork;

    /** Singular values */
    private double [] S;

    /**
     * Computes a partial SVD of a matrix. Before accessing results, perform computations
     * by calling the {@link #compute()}method.
     * 
     * @param A matrix to be factorized
     */
    public PartialSingularValueDecomposition(DoubleMatrix2D A)
    {
        super(A);

        this.k = DEFAULT_K;
    }

    public void compute()
    {
        // Need native LAPACK, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeLapackAvailable()
            || (!(A instanceof NNIDenseDoubleMatrix2D))
            || ((NNIDenseDoubleMatrix2D) A).isView())
        {
            // Use (slow) Colt's SVD
            SingularValueDecomposition svd;
            if (A.columns() > A.rows())
            {
                svd = new SingularValueDecomposition(A.viewDice());
                V = svd.getU();
                U = svd.getV();
            }
            else
            {
                svd = new SingularValueDecomposition(A);
                U = svd.getU();
                V = svd.getV();
            }

            S = svd.getSingularValues();

            if (k > 0 && k < S.length)
            {
                U = U.viewPart(0, 0, U.rows(), k);
                V = V.viewPart(0, 0, V.rows(), k);
                S = org.apache.mahout.math.Arrays.trimToCapacity(S, k);
            }
        }
        else
        {
            // Use (faster) native LAPACK
            int n = A.rows();
            int m = A.columns();

            // Not sure if can do that. The original version has (m, m), but
            // the remaining columns are zero anyway
            V = NNIDoubleFactory2D.nni.make(n, m);
            U = NNIDoubleFactory2D.nni.make(n, n);
            S = new double [Math.min(m, n)];

            init(m, n);

            // Copy the data array of the A matrix (LAPACK will overwrite the
            // input data)
            final double [] data = ((NNIDenseDoubleMatrix2D) A).getData();
            double [] dataA = Arrays.copyOf(data, data.length);

            int [] info = new int [1];

            NNIInterface.getLapack().gesdd(
                new char [] {'S'}, 
                new int [] {m}, 
                new int [] {n}, 
                dataA, 
                new int [] {Math.max(1, m)}, 
                S, 
                ((NNIDenseDoubleMatrix2D) V).getData(), 
                new int [] {Math.max(1, m)}, 
                ((NNIDenseDoubleMatrix2D) U).getData(), 
                new int []{Math.max(1, n)}, 
                work, 
                new int []{ work.length}, 
                iwork, info);

            // LAPACK calculates V' instead of V so need to do a deep transpose
            ((NNIDenseDoubleMatrix2D) V).transpose();

            if (k > 0 && k < S.length)
            {
                // Return an NNI dense matrix so that native operations are
                // possible
                DenseDoubleMatrix2D Uk = (DenseDoubleMatrix2D) NNIDoubleFactory2D.nni
                    .make(U.rows(), k);
                DenseDoubleMatrix2D Vk = (DenseDoubleMatrix2D) NNIDoubleFactory2D.nni
                    .make(V.rows(), k);
                Uk.assign(U.viewPart(0, 0, U.rows(), k));
                Vk.assign(V.viewPart(0, 0, V.rows(), k));

                U = Uk;
                V = Vk;
                S = org.apache.mahout.math.Arrays.trimToCapacity(S, k);
            }
        }
    }

    /**
     * Initialization for LAPACK-based calculations.
     */
    private void init(int m, int n)
    {
        // Find workspace requirements
        iwork = new int [8 * Math.min(m, n)];

        // Query optimal workspace
        work = new double [1];
        int [] info = new int [1];

        NNIInterface.getLapack().gesdd(
            new char [] { 'S' }, 
            new int [] { m }, 
            new int [] { n }, 
            new double [0], 
            new int [] {Math.max(1, m)}, 
            new double [0], 
            new double [0], 
            new int [] {Math.max(1, m)}, 
            new double [0], 
            new int [] {Math.max(1, n)}, 
            work, 
            new int [] {-1}, 
            iwork, 
            info);

        // Allocate workspace
        int lwork = -1;
        if (info[0] != 0)
        {
            lwork = 3
                * Math.min(m, n)
                * Math.min(m, n)
                + Math.max(Math.max(m, n), 4 * Math.min(m, n) * Math.min(m, n) + 4
                    * Math.min(m, n));
        }
        else
        {
            lwork = (int) work[0];
        }

        lwork = Math.max(lwork, 1);
        work = new double [lwork];
    }

    public String toString()
    {
        return "nni-SVD";
    }

    /**
     * Returns singular values of the matrix.
     */
    public double [] getSingularValues()
    {
        return S;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     * 
     * @param k the number of base vectors
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK()
    {
        return k;
    }
}
