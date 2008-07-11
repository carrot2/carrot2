package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * A factory of {@link MatrixFactorization}s.
 */
public interface MatrixFactorizationFactory
{
    /**
     * Factorizes matrix <code>A</code>.
     * 
     * @param A matrix to be factorized.
     */
    public MatrixFactorization factorize(DoubleMatrix2D A);
}