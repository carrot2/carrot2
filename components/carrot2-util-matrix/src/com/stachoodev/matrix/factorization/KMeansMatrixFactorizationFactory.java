/*
 * NonnegativeMatrixFactorizationEDFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class KMeansMatrixFactorizationFactory extends
    IterativeMatrixFactorizationFactory implements MatrixFactorizationFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.matrix.factorization.MatrixFactorizationFactory#factorize(cern.colt.matrix.DoubleMatrix2D)
     */
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        KMeansMatrixFactorization factorization = new KMeansMatrixFactorization(
            A);
        factorization.setK(k);
        factorization.setMaxIterations(maxIterations);
        factorization.setStopThreshold(stopThreshold);
        factorization.setDoubleFactory2D(getDoubleFactory2D());

        factorization.compute();
        
        return factorization;
    }
}