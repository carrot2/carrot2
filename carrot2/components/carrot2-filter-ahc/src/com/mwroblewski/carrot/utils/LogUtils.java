
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.mwroblewski.carrot.utils;


import java.util.Date;


/**
 * Class containing static methods useful for creating logging messages. (eg. logging contents of
 * an array or length of a period of time since given date)
 *
 * @author Micha� Wr�blewski
 */
public class LogUtils
{
    /**
     * Utility method measuring length of period of time since given date till now and converting
     * it to <code>String</code>
     *
     * @param from beginning of period of time
     *
     * @return <code>String</code> representing length of period of time since <code>from</code>
     *         till now (in miliseconds)
     */
    public static String timeTillNow(Date from)
    {
        Date now = new Date();
        long diff = now.getTime() - from.getTime();

        return (diff + "");
    }


    public static String arrayToString(float [] array)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < array.length; i++)
        {
            result.append(array[i]);
            result.append(" ");
        }

        return result.toString();
    }


    /**
     * Utility method converting to <code>String</code> contents of given two-dimensional array of
     * <code>float</code> numbers. Method separates the numbers with spaces and inserts the
     * newline character <b>before</b> each row of the array.
     *
     * @param array two-dimensional array of <code>float</code> numbers
     *
     * @return <code>String</code> representation of <code>array</code> parameter
     */
    public static String arrayToString(float [][] array)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < array.length; i++)
        {
            result.append("\n");

            for (int j = 0; j < array[i].length; j++)
            {
                result.append(array[i][j]);
                result.append(" ");
            }
        }

        return result.toString();
    }


    public static String arrayToString(int [] array)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < array.length; i++)
        {
            result.append(array[i]);
            result.append(" ");
        }

        return result.toString();
    }


    /**
     * Utility method converting to <code>String</code> contents of given two-dimensional array of
     * <code>int</code> numbers. Method separates the numbers with spaces and inserts the newline
     * character <b>before</b> each row of the array.
     *
     * @param array two-dimensional array of <code>int</code> numbers
     *
     * @return <code>String</code> representation of <code>array</code> parameter
     */
    public static String arrayToString(int [][] array)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < array.length; i++)
        {
            result.append("\n");

            for (int j = 0; j < array[i].length; j++)
            {
                result.append(array[i][j]);
                result.append(" ");
            }
        }

        return result.toString();
    }
}
