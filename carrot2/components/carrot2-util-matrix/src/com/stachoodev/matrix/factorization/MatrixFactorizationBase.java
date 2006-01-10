
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

import cern.colt.matrix.*;

/**
 * Abstract implementation of the MatrixFactorization interface. 
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class MatrixFactorizationBase implements
    MatrixFactorization
{

    /** The matrix factory to be used when creating the result matrices */
    protected DoubleFactory2D doubleFactory2D;
    protected static DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /** Input and output matrices */
    protected DoubleMatrix2D A;
    protected DoubleMatrix2D U;
    protected DoubleMatrix2D V;

    /**
     * Creates the MatrixFactorizationBase object for matrix A. All
     * computations will be performed during the invocation of the constructor.
     * By default RandomMatrixFactorizationSeeding will be used.
     * 
     * @param A matrix to be factorized
     * @param k the desired number of base vectors
     */
    public MatrixFactorizationBase(DoubleMatrix2D A)
    {
        this.A = A;
        this.doubleFactory2D = DEFAULT_DOUBLE_FACTORY_2D;
    }

    /**
     * Returns the matrix factory used when creating the result matrices.
     * 
     * @return
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

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorization#getU()
     */
    public DoubleMatrix2D getU()
    {
        return U;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorization#getV()
     */
    public DoubleMatrix2D getV()
    {
        return V;
    }

    /**
     * Computes the factorization.
     */
    public abstract void compute();
}