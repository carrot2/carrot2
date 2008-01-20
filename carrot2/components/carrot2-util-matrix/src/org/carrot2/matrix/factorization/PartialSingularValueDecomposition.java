
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization;

import nni.LAPACK;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.NNIDoubleFactory2D;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.linalg.SingularValueDecomposition;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PartialSingularValueDecomposition extends MatrixFactorizationBase
    implements MatrixFactorization
{
    /** The desired number of base vectors */
    protected int k;
    protected static int DEFAULT_K = -1;

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
     * Computes a partial SVD of a matrix. Before accessing results, perform
     * computations by calling the {@link #compute()}method.
     * 
     * @param A
     */
    public PartialSingularValueDecomposition(DoubleMatrix2D A)
    {
        super(A);

        this.k = DEFAULT_K;
    }

    /*
     * (non-Javadoc)
     */
    public void compute()
    {
        // Need native LAPACK, dense matrices and no views to operate
        // Default to Colt's implementation otherwise
        if (!NNIInterface.isNativeLapackAvailable()
            || (!(A instanceof DenseDoubleMatrix2D))
            || NNIDenseDoubleMatrix2D.isView((DenseDoubleMatrix2D) A))
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
                S = cern.colt.Arrays.trimToCapacity(S, k);
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
            double [] dataA = new double [NNIDenseDoubleMatrix2D
                .getDoubleData((DenseDoubleMatrix2D) A).length];
            System.arraycopy(NNIDenseDoubleMatrix2D
                .getDoubleData((DenseDoubleMatrix2D) A), 0, dataA, 0,
                dataA.length);

            int [] info = new int [1];
            LAPACK.gesdd(new char []
            { 'S' }, new int []
            { m }, new int []
            { n }, dataA, new int []
            { Math.max(1, m) }, S, NNIDenseDoubleMatrix2D
                .getDoubleData((DenseDoubleMatrix2D) V), new int []
            { Math.max(1, m) }, NNIDenseDoubleMatrix2D
                .getDoubleData((DenseDoubleMatrix2D) U), new int []
            { Math.max(1, n) }, work, new int []
            { work.length }, iwork, info);

            // LAPACK calculates V' instead of V so need to do a deep transpose
            NNIDenseDoubleMatrix2D.deepTranspose((DenseDoubleMatrix2D) V);

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
                S = cern.colt.Arrays.trimToCapacity(S, k);
            }
        }
    }

    /**
     * @param m
     * @param n
     */
    private void init(int m, int n)
    {
        // Find workspace requirements
        iwork = new int [8 * Math.min(m, n)];

        // Query optimal workspace
        work = new double [1];
        int [] info = new int [1];
        LAPACK.gesdd(new char []
        { 'S' }, new int []
        { m }, new int []
        { n }, new double [0], new int []
        { Math.max(1, m) }, new double [0], new double [0], new int []
        { Math.max(1, m) }, new double [0], new int []
        { Math.max(1, n) }, work, new int []
        { -1 }, iwork, info);

        // Allocate workspace
        int lwork = -1;
        if (info[0] != 0)
        {
            lwork = 3
                * Math.min(m, n)
                * Math.min(m, n)
                + Math.max(Math.max(m, n), 4 * Math.min(m, n) * Math.min(m, n)
                    + 4 * Math.min(m, n));
        }
        else
        {
            lwork = (int) work[0];
        }

        lwork = Math.max(lwork, 1);
        work = new double [lwork];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "nni-SVD";
    }

    /**
     * Returns singular values of the matrix. 
     * 
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
     * 
     */
    public int getK()
    {
        return k;
    }
}