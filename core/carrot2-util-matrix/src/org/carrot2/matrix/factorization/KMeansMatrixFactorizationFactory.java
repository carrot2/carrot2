package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * {@link KMeansMatrixFactorization} factory.
 */
public class KMeansMatrixFactorizationFactory extends IterativeMatrixFactorizationFactory
    implements MatrixFactorizationFactory
{
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        KMeansMatrixFactorization factorization = new KMeansMatrixFactorization(A);
        factorization.setK(k);
        factorization.setMaxIterations(maxIterations);
        factorization.setStopThreshold(stopThreshold);
        factorization.setDoubleFactory2D(getDoubleFactory2D());

        factorization.compute();

        return factorization;
    }
}