package org.carrot2.matrix.factorization;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * For an <i>m</i> &times; <i>n</i> matrix <i>A</i> and given <i>k</i>, computes an <i>m
 * </i> &times; <i>k</i> matrix <i>U</i> and <i>k</i> &times; <i>n</i> matrix <i>V'</i>
 * such that <i>A ~= UV'</i>.
 */
public interface MatrixFactorization
{
    /**
     * Returns the U matrix (base vectors matrix).
     * 
     * @return U matrix
     */
    public abstract DoubleMatrix2D getU();

    /**
     * Returns the V matrix (coefficient matrix)
     * 
     * @return V matrix
     */
    public abstract DoubleMatrix2D getV();
}