

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.stachoodev.util.arrays;


/**
 * @author stachoo
 */
public class ArrayUtils
{
    /**
     * @param array
     *
     * @return String
     */
    public static String toString(int [] array)
    {
        if ((array == null) || (array.length == 0))
        {
            return "[ ]";
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append(Integer.toString(array[0]));

        for (int i = 1; i < array.length; i++)
        {
            stringBuffer.append(", ");
            stringBuffer.append(Integer.toString(array[i]));
        }

        stringBuffer.append("]");

        return stringBuffer.toString();
    }


    /**
     * @param array
     *
     * @return
     */
    public static int [] clone(int [] array)
    {
        if (array == null)
        {
            return null;
        }

        int [] clone = new int[array.length];

        System.arraycopy(array, 0, clone, 0, array.length);

        return clone;
    }


    /**
     * @param array
     *
     * @return
     */
    public static String [] clone(String [] array)
    {
        if (array == null)
        {
            return null;
        }

        String [] clone = new String[array.length];

        System.arraycopy(array, 0, clone, 0, array.length);

        return clone;
    }


    /**
     * @param array
     * @param newValue
     *
     * @return int[]
     */
    public static int [] extend(int [] array, int newValue)
    {
        if (array == null)
        {
            return null;
        }

        int [] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[newArray.length - 1] = newValue;

        return newArray;
    }


    /**
     * @param array
     * @param boundaries
     *
     * @return
     */
    public static int [][] split(int [] array, int [] boundaries)
    {
        int [][] split = new int[boundaries.length - 1][];

        for (int i = 0; i < split.length; i++)
        {
            split[i] = new int[0];
        }

        if (array.length > 0)
        {
            for (int i = 0; i < (boundaries.length - 1); i++)
            {
                split[i] = new int[boundaries[i + 1] - boundaries[i]];
                System.arraycopy(array, boundaries[i], split[i], 0, split[i].length);
            }
        }

        return split;
    }


    /**
     * @param array
     * @param projection
     *
     * @return double[]
     */
    public static double [] project(double [] array, int [] projection)
    {
        if (array == null)
        {
            return null;
        }

        if (projection == null)
        {
            return array;
        }

        double [] projected = new double[projection.length];

        for (int i = 0; i < projected.length; i++)
        {
            projected[i] = array[projection[i]];
        }

        return projected;
    }


    /**
     * @param array
     * @param projection
     *
     * @return int[]
     */
    public static int [] project(int [] array, int [] projection)
    {
        if (array == null)
        {
            return null;
        }

        if (projection == null)
        {
            return array;
        }

        int [] projected = new int[projection.length];

        for (int i = 0; i < projected.length; i++)
        {
            projected[i] = array[projection[i]];
        }

        return projected;
    }


    /**
     * @param array
     * @param projection
     *
     * @return boolean[]
     */
    public static boolean [] project(boolean [] array, int [] projection)
    {
        if (array == null)
        {
            return null;
        }

        if (projection == null)
        {
            return array;
        }

        boolean [] projected = new boolean[projection.length];

        for (int i = 0; i < projected.length; i++)
        {
            projected[i] = array[projection[i]];
        }

        return projected;
    }


    /**
     * @param array
     * @param value
     *
     * @return boolean
     */
    public static boolean contains(int [] array, int value)
    {
        if (array == null)
        {
            return false;
        }

        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value)
            {
                return true;
            }
        }

        return false;
    }


    /**
     * @param array
     *
     * @return
     */
    public static double average(double [] array)
    {
        if ((array == null) || (array.length == 0))
        {
            return 0;
        }

        double sum = 0;

        for (int i = 0; i < array.length; i++)
        {
            sum += array[i];
        }

        return sum / array.length;
    }


    /**
     * @param array
     *
     * @return
     */
    public static double stdDev(double [] array)
    {
        if ((array == null) || (array.length == 0))
        {
            return 0;
        }

        double average = average(array);
        double dev = 0;

        for (int i = 0; i < array.length; i++)
        {
            dev += ((array[i] - average) * (array[i] - average));
        }

        return Math.sqrt(dev / array.length);
    }
}
