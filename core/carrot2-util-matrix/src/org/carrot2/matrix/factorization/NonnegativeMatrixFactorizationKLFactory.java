package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Factory for {@link NonnegativeMatrixFactorizationKL}s.
 */
public class NonnegativeMatrixFactorizationKLFactory extends
    IterativeMatrixFactorizationFactory
{
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        NonnegativeMatrixFactorizationKL factorization = new NonnegativeMatrixFactorizationKL(
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