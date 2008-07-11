package org.carrot2.matrix;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * FEST-style assertions for Colt matrices.
 */
public class MatrixAssertions
{
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
