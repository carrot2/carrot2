
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

/**
 * Assertions on <code>int [][]</code> arrays.
 */
public class IntIntArrayAssert implements AssertExtension
{
    /** The actual array */
    private int [][] actualArray;

    /** Description of the assertion */
    private String description;

    IntIntArrayAssert(int [][] array)
    {
        this.actualArray = array;
    }

    /**
     * Asserts that the array is equal to the provided
     */
    public IntIntArrayAssert isEqualTo(int [][] expected)
    {
        assertThat(expected).as(description).isNotNull();
        assertThat(actualArray.length).as(description).isEqualTo(expected.length);

        for (int i = 0; i < expected.length; i++)
        {
            assertThat(actualArray[i]).as(description + "[" + i + "]").isEqualTo(
                expected[i]);
        }
        return this;
    }

    public IntIntArrayAssert as(String description)
    {
        this.description = description;
        return this;
    }
}
