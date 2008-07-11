package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * A factory for {@link NonnegativeMatrixFactorizationED}s.
 */
public class NonnegativeMatrixFactorizationEDFactory extends
    IterativeMatrixFactorizationFactory
{
    public MatrixFactorization factorize(DoubleMatrix2D A)
    {
        NonnegativeMatrixFactorizationED factorization = new NonnegativeMatrixFactorizationED(
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