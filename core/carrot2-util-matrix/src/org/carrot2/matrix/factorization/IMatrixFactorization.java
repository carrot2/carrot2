
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization;

import org.carrot2.mahout.math.matrix.*;

/**
 * For an <i>m</i> &times; <i>n</i> matrix <i>A</i> and given <i>k</i>, computes an <i>m
 * </i> &times; <i>k</i> matrix <i>U</i> and <i>k</i> &times; <i>n</i> matrix <i>V'</i>
 * such that <i>A ~= UV'</i>.
 */
public interface IMatrixFactorization
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
