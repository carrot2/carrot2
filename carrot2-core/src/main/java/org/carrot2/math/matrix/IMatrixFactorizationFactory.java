
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.math.matrix;

import org.carrot2.math.mahout.matrix.*;
import org.carrot2.attrs.AcceptingVisitor;

/**
 * A factory of {@link IMatrixFactorization}s.
 */
public interface IMatrixFactorizationFactory extends AcceptingVisitor {
    /**
     * Factorizes matrix <code>A</code>.
     * 
     * @param A matrix to be factorized.
     */
    IMatrixFactorization factorize(DoubleMatrix2D A);
}
