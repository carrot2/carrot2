
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
 * A factory of {@link IMatrixFactorization}s.
 */
public interface IMatrixFactorizationFactory
{
    /**
     * Factorizes matrix <code>A</code>.
     * 
     * @param A matrix to be factorized.
     */
    public IMatrixFactorization factorize(DoubleMatrix2D A);
}
