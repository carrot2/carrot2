
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
 * A simple utility for calculating linear approximations based on values for equally
 * distributed points between minimum and maximum arguments.
 */
public class LinearApproximation
{
    /** Values to base the approximation on */
    private double [] points;

    /** Minimum argument value */
    private double argMin;

    /** Maximum argument value */
    private double argMax;

    /** Step between value points */
    private double step;

    /** Argument value ranges */
    private double [] arguments;

    public LinearApproximation(double [] points, double argMin, double argMax)
    {
        this.points = points;
        this.argMin = argMin;
        this.argMax = argMax;

        arguments = new double [points.length];
        step = (argMax - argMin) / (points.length - 1);

        for (int i = 0; i < arguments.length; i++)
        {
            arguments[i] = argMin + step * i;
        }
    }

    /**
     * Returns approximated value for the provided argument.
     */
    public double getValue(double argument)
    {
        if (points.length == 1)
        {
            return points[0];
        }

        if (argument <= arguments[0])
        {
            return points[0];
        }
        else if (argument >= arguments[arguments.length - 1])
        {
            return points[points.length - 1];
        }
        else
        {
            int bucket = (int) ((points.length - 1) * (argument - argMin) / (argMax - argMin));
            return points[bucket] + ((argument - arguments[bucket]) / step)
                * (points[bucket + 1] - points[bucket]);
        }
    }
}
