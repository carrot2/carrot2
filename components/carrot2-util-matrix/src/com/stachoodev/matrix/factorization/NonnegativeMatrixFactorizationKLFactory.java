/*
 * NonnegativeMatrixFactorizationEDFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class NonnegativeMatrixFactorizationKLFactory extends
    IterativeMatrixFactorizationFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationFactory#factorize(cern.colt.matrix.DoubleMatrix2D)
     */
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        NonnegativeMatrixFactorizationKL factorization = new NonnegativeMatrixFactorizationKL(
            A);
        factorization.setK(k);
        factorization.setMaxIterations(maxIterations);
        factorization.setStopThreshold(stopThreshold);
        factorization.setSeedingStrategy(createSeedingStrategy());
        factorization.setDoubleFactory2D(getDoubleFactory2D());

        factorization.compute();

        return factorization;
    }
}