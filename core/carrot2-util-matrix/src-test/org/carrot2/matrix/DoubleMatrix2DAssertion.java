
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

package org.carrot2.matrix;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.AssertExtension;
import org.fest.assertions.Delta;
import org.carrot2.mahout.math.matrix.*;

/**
 * Assertions on <code>DoubleMatrix2D</code>.
 */
public class DoubleMatrix2DAssertion implements AssertExtension
{
    /** The actual matrix */
    private DoubleMatrix2D actualMatrix;

    /** Assertion description */
    private String description = "element";

    DoubleMatrix2DAssertion(DoubleMatrix2D actualMatrix)
    {
        this.actualMatrix = actualMatrix;
    }

    /**
     * Asserts that the matrix is equivalent to the provided array of values.
     */
    public DoubleMatrix2DAssertion isEquivalentTo(double [][] values)
    {
        return isEquivalentTo(values, 0);
    }

    /**
     * Asserts that the matrix is equivalent to the provided matrix.
     */
    public DoubleMatrix2DAssertion isEquivalentTo(DoubleMatrix2D expected)
    {
        return isEquivalentTo(expected.toArray(), 0);
    }

    /**
     * Asserts that the matrix is equivalent to the provided matrix with the
     * <code>delta</code> error margin per element.
     */
    public DoubleMatrix2DAssertion isEquivalentTo(DoubleMatrix2D expected, double delta)
    {
        return isEquivalentTo(expected.toArray(), delta);
    }

    /**
     * Asserts that the matrix is equivalent to the provided array of values with the
     * <code>delta</code> error margin per element.
     */
    public DoubleMatrix2DAssertion isEquivalentTo(double [][] values, double delta)
    {
        assertThat(actualMatrix).isNotNull();
        assertThat(actualMatrix.rows()).as("rows").isEqualTo(values.length);

        final Delta deltaObject = Delta.delta(delta);
        for (int row = 0; row < values.length; row++)
        {
            assertThat(actualMatrix.columns()).as("columns")
                .isEqualTo(values[row].length);
            for (int column = 0; column < values[row].length; column++)
            {
                assertThat(actualMatrix.get(row, column)).as(
                    description + "[" + row + ", " + column + "]").isEqualTo(
                    values[row][column], deltaObject);
            }
        }

        return this;
    }

    public DoubleMatrix2DAssertion as(String description)
    {
        this.description = description;
        return this;
    }
}
