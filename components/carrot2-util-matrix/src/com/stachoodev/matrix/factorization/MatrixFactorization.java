/*
 * MatrixFactorization.java
 * 
 * Created on 2004-05-09
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * For an m x n matrix A and given k, computes an m x k matrix U and k x n
 * matrix V' such that A ~= UV'.
 * 
 * @author stachoo
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