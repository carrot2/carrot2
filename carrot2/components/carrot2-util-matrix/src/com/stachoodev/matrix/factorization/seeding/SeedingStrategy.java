/*
 * SeedingStrategy.java
 * 
 * Created on 2004-05-12
 */
package com.stachoodev.matrix.factorization.seeding;

import cern.colt.matrix.*;

/**
 * Defines the seeding routine to be used as part of a matrix factorisation
 * algorithm.
 * 
 * @author stachoo
 */
public interface SeedingStrategy
{

    /**
     * Initialises values of the provided U and V matrices. The A matrix is the
     * input matrix to be factorized.
     * 
     * @param A matrix to be factorized
     * @param U
     * @param V
     */
    public void seed(DoubleMatrix2D A, DoubleMatrix2D U, DoubleMatrix2D V);
}