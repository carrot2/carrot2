
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

import org.carrot2.matrix.NNIDoubleFactory2D;

import org.apache.mahout.math.matrix.*;

/**
 * Abstract implementation of the {@link IMatrixFactorization} interface.
 */
@SuppressWarnings("deprecation")
abstract class MatrixFactorizationBase implements IMatrixFactorization
{
    /** The matrix factory to be used when creating the result matrices */
    protected DoubleFactory2D doubleFactory2D;

    /** Default matrix factory */
    protected static DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /** Input matrix */
    protected DoubleMatrix2D A;

    /** Base vector result matrix */
    protected DoubleMatrix2D U;

    /** Coefficient result matrix */
    protected DoubleMatrix2D V;

    /**
     * Creates the MatrixFactorizationBase object for matrix A. All computations will be
     * performed during the invocation of the constructor. By default
     * RandomMatrixFactorizationSeeding will be used.
     * 
     * @param A matrix to be factorized
     */
    public MatrixFactorizationBase(DoubleMatrix2D A)
    {
        this.A = A;
        this.doubleFactory2D = DEFAULT_DOUBLE_FACTORY_2D;
    }

    /**
     * Returns the matrix factory used when creating the result matrices.
     */
    public DoubleFactory2D getDoubleFactory2D()
    {
        return doubleFactory2D;
    }

    /**
     * Sets the matrix factory to be used when creating the result matrices.
     * 
     * @param doubleFactory2D
     */
    public void setDoubleFactory2D(DoubleFactory2D doubleFactory2D)
    {
        this.doubleFactory2D = doubleFactory2D;
    }

    public DoubleMatrix2D getU()
    {
        return U;
    }

    public DoubleMatrix2D getV()
    {
        return V;
    }

    /**
     * Computes the factorization.
     */
    protected abstract void compute();
}
