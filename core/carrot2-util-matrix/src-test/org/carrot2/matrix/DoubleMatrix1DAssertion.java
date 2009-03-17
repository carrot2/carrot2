package org.carrot2.matrix;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.DoubleAssert;
import org.fest.assertions.DoubleAssert.Delta;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * Assertions on {@link DoubleMatrix1D}.
 */
public class DoubleMatrix1DAssertion
{
    /** The actual matrix */
    private DoubleMatrix1D actualMatrix;

    /** Assertion description */
    private String description = "element";

    DoubleMatrix1DAssertion(DoubleMatrix1D actualMatrix)
    {
        this.actualMatrix = actualMatrix;
    }

    /**
     * Asserts that the matrix is equivalent to the provided array of values.
     */
    public DoubleMatrix1DAssertion isEquivalentTo(double [] values)
    {
        return isEquivalentTo(values, 0);
    }

    /**
     * Asserts that the matrix is equivalent to the provided matrix.
     */
    public DoubleMatrix1DAssertion isEquivalentTo(DoubleMatrix1D expected)
    {
        return isEquivalentTo(expected.toArray(), 0);
    }

    /**
     * Asserts that the matrix is equivalent to the provided matrix with the
     * <code>delta</code> error margin per element.
     */
    public DoubleMatrix1DAssertion isEquivalentTo(DoubleMatrix1D expected, double delta)
    {
        return isEquivalentTo(expected.toArray(), delta);
    }

    /**
     * Asserts that the matrix is equivalent to the provided array of values with the
     * <code>delta</code> error margin per element.
     */
    public DoubleMatrix1DAssertion isEquivalentTo(double [] values, double delta)
    {
        assertThat(actualMatrix).isNotNull();
        assertThat(actualMatrix.size()).as("size").isEqualTo(values.length);

        final Delta deltaObject = DoubleAssert.delta(delta);
        for (int column = 0; column < values.length; column++)
        {
            assertThat(actualMatrix.get(column)).as(description + "[" + column + "]")
                .isEqualTo(values[column], deltaObject);
        }

        return this;
    }

    public DoubleMatrix1DAssertion as(String description)
    {
        this.description = description;
        return this;
    }
}
