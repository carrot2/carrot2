
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

import java.util.Arrays;

import org.carrot2.mahout.math.DenseMatrix;
import org.carrot2.mahout.math.Matrix;
import org.carrot2.mahout.math.SingularValueDecomposition;
import org.carrot2.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.mahout.math.matrix.impl.DenseDoubleMatrix2D;

/**
 * Performs matrix factorization using the Singular Value Decomposition algorithm.
 */
public class PartialSingularValueDecomposition extends MatrixFactorizationBase implements
    IMatrixFactorization
{
    /** The desired number of base vectors */
    protected int k;

    /** The default number of desired base vectors */
    protected static final int DEFAULT_K = -1;

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
        // Use Colt's SVD
        SingularValueDecomposition svd;
        if (A.columns() > A.rows())
        {
            svd = new SingularValueDecomposition(new DenseMatrix(A.viewDice().toArray()));
            V = toColtMatrix(svd.getU());
            U = toColtMatrix(svd.getV());
        }
        else
        {
            svd = new SingularValueDecomposition(new DenseMatrix(A.toArray()));
            U = toColtMatrix(svd.getU());
            V = toColtMatrix(svd.getV());
        }

        S = svd.getSingularValues();

        if (k > 0 && k < S.length)
        {
            U = U.viewPart(0, 0, U.rows(), k);
            V = V.viewPart(0, 0, V.rows(), k);
            S = Arrays.copyOf(S, k);
        }
    }

    private static DenseDoubleMatrix2D toColtMatrix(Matrix m)
    {
        DenseDoubleMatrix2D result = new DenseDoubleMatrix2D(m.rowSize(), m.columnSize());
        for (int r = 0; r < result.rows(); r++)
        {
            for (int c = 0; c < result.columns(); c++)
            {
                result.setQuick(r, c, m.getQuick(r, c));
            }            
        }
        return result;
    }
    
    public String toString()
    {
        return "SVD";
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
