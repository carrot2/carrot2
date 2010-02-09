
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

/**
 * Utilities for working with <code>int</code> arrays.
 */
public class IntArrayUtils
{
    /**
     * Encodes a sparse <code>int</code> array (with many zeros) into a more compact
     * format where even indices of the returned array correspond to the indices of
     * non-zero entries of
     * <code>array</array>, and odd indices contain the non-zero value.
     */
    public static int [] toSparseEncoding(int [] array)
    {
        int values = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] != 0)
            {
                values++;
            }
        }

        int [] result = new int [values * 2];
        int index = 0;
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] != 0)
            {
                result[index * 2] = i;
                result[index * 2 + 1] = array[i];
                index++;
            }
        }

        return result;
    }

    /**
     * Adds values from the <code>sparselyEncoded</code> array to the corresponding
     * elements of the non-sparsely coded <code>array</code>.
     */
    public static void addAllFromSparselyEncoded(int [] array, int [] sparselyEncoded)
    {
        for (int i = 0; i < sparselyEncoded.length / 2; i++)
        {
            array[sparselyEncoded[i * 2]] += sparselyEncoded[i * 2 + 1];
        }
    }

    /**
     * No instantiation
     */
    private IntArrayUtils()
    {
    }
}
