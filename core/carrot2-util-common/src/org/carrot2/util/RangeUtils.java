
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

package org.carrot2.util;

/**
 * A helper class for performing various calculations for integer and double ranges.
 */
public class RangeUtils
{
    /**
     * Calculates the spacing between minor (secondary) "ticks" of a slider.
     * 
     * @param min minimum value of the range
     * @param max maximum value of the range
     */
    public static int getIntMinorTicks(int min, int max)
    {
        int majorTicks = getIntMajorTicks(min, max);

        if ((majorTicks % 2) == 0)
        {
            return majorTicks / 2;
        }
        else
        {
            return majorTicks;
        }
    }

    /**
     * Calculates the spacing between major (primary) "ticks" of a slider.
     * 
     * @param min minimum value of the range
     * @param max maximum value of the range
     */
    public static int getIntMajorTicks(int min, int max)
    {
        int diff = max - min;
        if (diff < 0)
        {
            diff = -diff;
        }

        if (diff <= 10)
        {
            return 1;
        }

        int magnitude = (int) (Math.log(diff - 1) / Math.log(10));
        int bucket = (int) (diff / Math.pow(10, magnitude));

        if (bucket <= 2)
        {
            return (int) (5 * Math.pow(10, magnitude - 1));
        }
        else if (bucket <= 5)
        {
            return (int) (10 * Math.pow(10, magnitude - 1));
        }
        else
        {
            return (int) (20 * Math.pow(10, magnitude - 1));
        }
    }

    /**
     * Calculates the spacing between minor (secondary) "ticks" of a slider.
     * 
     * @param min minimum value of the range
     * @param max maximum value of the range
     */
    public static double getDoubleMinorTicks(double min, double max)
    {
        return getDoubleMajorTicks(min, max) / 2;
    }

    /**
     * Calculates the spacing between major (primary) "ticks" of a slider.
     * 
     * @param min minimum value of the range
     * @param max maximum value of the range
     */
    public static double getDoubleMajorTicks(double min, double max)
    {
        double diff = max - min;
        if (diff < 0)
        {
            diff = -diff;
        }

        if (diff <= 1.0)
        {
            return 0.2;
        }

        int magnitude = (int) (Math.log(diff - 0.00001) / Math.log(10));
        int bucket = (int) (diff / Math.pow(10, magnitude));

        if (bucket <= 2)
        {
            return (5 * Math.pow(10, magnitude - 1));
        }
        else if (bucket <= 5)
        {
            return (10 * Math.pow(10, magnitude - 1));
        }
        else
        {
            return (20 * Math.pow(10, magnitude - 1));
        }
    }
}
