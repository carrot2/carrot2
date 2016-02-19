
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

package org.carrot2.core.test;

import static org.fest.assertions.Assertions.assertThat;

import org.fest.assertions.AssertExtension;
import org.fest.assertions.Delta;

/**
 * Additional assertions on <code>double []</code> arrays.
 */
public class DoubleArrayAssert implements AssertExtension
{
    /** The actual array */
    private double [] actualArray;

    /** Description of the assertion */
    private String description;

    DoubleArrayAssert(double [] array)
    {
        this.actualArray = array;
    }

    /**
     * Asserts that the array is equal to the provided with a <code>delta</code> on each
     * element.
     */
    public DoubleArrayAssert isEqualTo(double [] expected, double delta)
    {
        assertThat(expected).as(description).isNotNull();
        assertThat(actualArray.length).as(description).isEqualTo(expected.length);

        final Delta deltaObject = Delta.delta(delta);
        for (int i = 0; i < expected.length; i++)
        {
            assertThat(actualArray[i]).as(description + "[" + i + "]").isEqualTo(
                expected[i], deltaObject);
        }
        return this;
    }

    public DoubleArrayAssert as(String description)
    {
        this.description = description;
        return this;
    }
}
