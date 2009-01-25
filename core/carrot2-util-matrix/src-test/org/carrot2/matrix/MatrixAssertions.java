
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * FEST-style assertions for Colt matrices.
 */
public class MatrixAssertions
{
    /**
     * Creates a {@link DoubleMatrix1DAssertion}.
     * 
     * @param actualMatrix
     * @return the assertion
     */
    public static DoubleMatrix1DAssertion assertThat(DoubleMatrix1D actualMatrix)
    {
        return new DoubleMatrix1DAssertion(actualMatrix);
    }
    /**
     * Creates a {@link DoubleMatrix2DAssertion}.
     * 
     * @param actualMatrix
     * @return the assertion
     */
    public static DoubleMatrix2DAssertion assertThat(DoubleMatrix2D actualMatrix)
    {
        return new DoubleMatrix2DAssertion(actualMatrix);
    }
}
