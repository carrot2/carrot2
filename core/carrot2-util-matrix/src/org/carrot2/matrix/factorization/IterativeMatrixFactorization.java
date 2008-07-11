package org.carrot2.matrix.factorization;

/**
 * Performs an iterative matrix factorization.
 */
public interface IterativeMatrixFactorization extends MatrixFactorization
{
    /**
     * Returns approximation error achieved after the last iteration of the
     * algorithm or -1 if the approximation error is not available.
     * 
     * @return approximation error or -1
     */
    public abstract double getApproximationError();

    /**
     * Returns the number of iterations the algorithm has completed.
     * 
     * @return the number of iterations the algorithm has completed
     */
    public abstract int getIterationsCompleted();
}