/*
 * NonnegativeMatrixFactorizationEDFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class LocalNonnegativeMatrixFactorizationFactory extends
    IterativeMatrixFactorizationFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationFactory#factorize(cern.colt.matrix.DoubleMatrix2D)
     */
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        LocalNonnegativeMatrixFactorization factorization = new LocalNonnegativeMatrixFactorization(
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