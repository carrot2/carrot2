/*
 * MatrixFactorizationFactory.java Created on 2004-06-17
 */
package com.stachoodev.matrix.factorization;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public interface MatrixFactorizationFactory
{
    /**
     * Factorizes matrix <code>A</code>.
     * 
     * @param A
     * @return
     */
    public MatrixFactorization factorize(DoubleMatrix2D A);
}