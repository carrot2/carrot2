package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * {@link LocalNonnegativeMatrixFactorization} factory.
 */
public class LocalNonnegativeMatrixFactorizationFactory extends
    IterativeMatrixFactorizationFactory
{
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        LocalNonnegativeMatrixFactorization factorization = new LocalNonnegativeMatrixFactorization(
            A);
        factorization.setK(k);
        factorization.setMaxIterations(maxIterations);
        factorization.setStopThreshold(stopThreshold);
        factorization.setSeedingStrategy(createSeedingStrategy());
        factorization.setDoubleFactory2D(getDoubleFactory2D());
        factorization.setOrdered(ordered);

        factorization.compute();

        return factorization;
    }
}