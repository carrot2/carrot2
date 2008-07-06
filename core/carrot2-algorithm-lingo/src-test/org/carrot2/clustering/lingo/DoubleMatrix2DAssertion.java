package org.carrot2.clustering.lingo;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.AssertExtension;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Assertions on {@link DoubleMatrix2D}.
 */
public class DoubleMatrix2DAssertion implements AssertExtension
{
    /** The actual matrix */
    private DoubleMatrix2D actualMatrix;

    DoubleMatrix2DAssertion(DoubleMatrix2D actualMatrix)
    {
        this.actualMatrix = actualMatrix;
    }

    /**
     * Asserts that the matrix is equivalent to the provided array of values.
     */
    public DoubleMatrix2DAssertion isEquivalentTo(double [][] values)
    {
        assertThat(actualMatrix).isNotNull();
        assertThat(actualMatrix.rows()).as("rows").isEqualTo(values.length);

        for (int row = 0; row < values.length; row++)
        {
            assertThat(actualMatrix.columns()).as("columns").isEqualTo(values[row].length);
            for (int column = 0; column < values[row].length; column++)
            {
                assertThat(actualMatrix.get(row, column)).as(
                    "element[" + row + ", " + column + "]")
                    .isEqualTo(values[row][column]);
            }
        }

        return this;
    }
}
