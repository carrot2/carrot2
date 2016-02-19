
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

import org.carrot2.mahout.math.matrix.DoubleMatrix2D;

/**
 * Abstract implementation of the {@link IMatrixFactorization} interface.
 */
abstract class MatrixFactorizationBase implements IMatrixFactorization
{
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
