package org.carrot2.matrix.factorization.seeding;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Defines the seeding routine to be used as part of a matrix factorization
 * algorithm.
 */
public interface SeedingStrategy
{
    /**
     * Initializes values of the provided U and V matrices. The A matrix is the
     * input matrix to be factorized.
     * 
     * @param A matrix to be factorized
     * @param U left factorized matrix to be seeded
     * @param V right factorized matrix to be seeded
     */
    public void seed(DoubleMatrix2D A, DoubleMatrix2D U, DoubleMatrix2D V);
}