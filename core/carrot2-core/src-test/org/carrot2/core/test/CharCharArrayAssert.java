
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
 * Assertions on <code>char [][]</code> arrays.
 */
public class CharCharArrayAssert implements AssertExtension
{
    /** The actual array */
    private char [][] actualArray;

    /** Description of the assertion */
    private String description;

    CharCharArrayAssert(char [][] array)
    {
        this.actualArray = array;
    }

    /**
     * Asserts that the array is equal to the provided
     */
    public CharCharArrayAssert isEqualTo(char [][] expected)
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

    public CharCharArrayAssert as(String description)
    {
        this.description = description;
        return this;
    }
}
