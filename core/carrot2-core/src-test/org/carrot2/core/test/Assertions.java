
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

/**
 * Additional FEST-style assertions.
 */
public class Assertions
{
    /**
     * Creates a {@link CharCharArrayAssert}.
     * 
     * @param actual the array to make assertions on.
     */
    public static CharCharArrayAssert assertThat(char [][] actual)
    {
        return new CharCharArrayAssert(actual);
    }

    /**
     * Creates an {@link IntIntArrayAssert}.
     */
    public static IntIntArrayAssert assertThat(int [][] actual)
    {
        return new IntIntArrayAssert(actual);
    }

    /**
     * Creates an {@link IntIntArrayAssert}.
     */
    public static ByteByteArrayAssert assertThat(byte [][] actual)
    {
        return new ByteByteArrayAssert(actual);
    }

    /**
     * Creates a {@link DoubleArrayAssert}.
     */
    public static DoubleArrayAssert assertThat(double [] actual)
    {
        return new DoubleArrayAssert(actual);
    }
}
