/*
 * IterativeMatrixFactorization.java
 * 
 * Created on 2004-06-20
 */
package com.stachoodev.matrix.factorization;

/**
 * @author stachoo
 */
public interface IterativeMatrixFactorization extends MatrixFactorization
{
    /**
     * Returns approximation error achieved after the last iteration of the
     * algorithm.
     * 
     * @return Returns the approximationError.
     */
    public abstract double getApproximationError();

    /**
     * Returns the number of iterations the algorithm has completed.
     * 
     * @return Returns the iterationsCompleted.
     */
    public abstract int getIterationsCompleted();
}