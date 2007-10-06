
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

/**
 * Array utility methods.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ArrayUtils
{
    /**
     * Reverses an <code>int</code> array in place. A reference to the input
     * array is returned for convenience.
     * 
     * @param array
     */
    public static int [] reverse(int [] array)
    {
        int temp;

        for (int i = 0; i < (array.length / 2); i++)
        {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }

        return array;
    }

    /**
     * Reverses a <code>double</code> array in place. A reference to the input
     * array is returned for convenience.
     * 
     * @param array
     */
    public static double [] reverse(double [] array)
    {
        double temp;

        for (int i = 0; i < (array.length / 2); i++)
        {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }

        return array;
    }
    
    /**
     * Reverses a <code>String</code> array in place. A reference to the input
     * array is returned for convenience.
     * 
     * @param array
     */
    public static String [] reverse(String [] array)
    {
        String temp;
        
        for (int i = 0; i < (array.length / 2); i++)
        {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
        
        return array;
    }

    /**
     * Calculates the mean value of the <b>non-zero </b> elements of
     * <code>a</code>.
     * 
     * @param a
     * @return mean value of the <b>non-zero </b> elements of <code>a</code>
     */
    public static double meanNonZero(double [] a)
    {
        double sum = 0;
        int count = 0;

        for (int i = 0; i < a.length; i++)
        {
            if (a[i] != 0)
            {
                count++;
                sum += a[i];
            }
        }

        return sum / count;
    }

    /**
     * Calculates the standard deviation of the <b>non-zero </b> elements of
     * <code>a</code>.
     * 
     * @param a
     * @return standard deviation of the <b>non-zero </b> elements of
     *         <code>a</code>
     */
    public static double stdDevNonZero(double [] a)
    {
        double mean = meanNonZero(a);

        double sum = 0;
        int count = 0;

        for (int i = 0; i < a.length; i++)
        {
            if (a[i] != 0)
            {
                count++;
                sum += (a[i] - mean) * (a[i] - mean);
            }
        }

        return Math.sqrt(sum / count);
    }

    /**
     * Return index of the first maxiumum element in <code>a</code>. If
     * <code>a</code> is null of has length 0, -1 will be returned.
     * 
     * @param a
     * @return index of the first maxiumum element in <code>a</code> or -1
     */
    public static int max(double [] a)
    {
        if (a == null || a.length == 0)
        {
            return -1;
        }

        double max = a[0];
        int index = 0;

        for (int i = 1; i < a.length; i++)
        {
            if (max < a[i])
            {
                max = a[i];
                index = i;
            }
        }

        return index;
    }
    
    /**
     * Return index of the first maxiumum element in <code>a</code>. If
     * <code>a</code> is null of has length 0, -1 will be returned.
     * 
     * @param a
     * @return index of the first maxiumum element in <code>a</code> or -1
     */
    public static int max(int [] a)
    {
        if (a == null || a.length == 0)
        {
            return -1;
        }
        
        double max = a[0];
        int index = 0;
        
        for (int i = 1; i < a.length; i++)
        {
            if (max < a[i])
            {
                max = a[i];
                index = i;
            }
        }
        
        return index;
    }
    
    public static String toString(Object [] array, String delimiter)
    {
        StringBuffer sb = new StringBuffer();
        
        if (array != null && array.length > 0)
        {
            sb.append(array[0].toString());
            for (int i = 1; i < array.length; i++)
            {
                sb.append(delimiter);
                sb.append(array[i]);
            }
        }
        
        return sb.toString();
    }
    
    public static String toString(Object [] array)
    {
        return toString(array, ", ");
    }
}