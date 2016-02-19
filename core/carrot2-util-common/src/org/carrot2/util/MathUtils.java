
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
 * Some simple mathematical computation utility methods.
 */
public final class MathUtils
{
    private MathUtils()
    {
    }

    /**
     * Returns the harmonic mean of <code>v1</code> and <code>v2</code>, giving both
     * arguments equal weight.
     */
    public static double harmonicMean(double v1, double v2)
    {
        return harmonicMean(v1, v2, 1, 1);
    }

    /**
     * Returns the harmonic mean of <code>v1</code> and <code>v2</code>, weighting them by
     * <code>w1</code> and <code>w2</code>, respectively.
     */
    public static double harmonicMean(double v1, double v2, double w1, double w2)
    {
        return (w1 + w2) / (w1 / v1 + w2 / v2);
    }

    /**
     * Returns the arithmetic mean of <code>v1</code> and <code>v2</code>, weighting them
     * by <code>w1</code> and <code>w2</code>, respectively.
     */
    public static double arithmeticMean(double v1, double v2, double w1, double w2)
    {
        return (v1 * w1 + v2 * w2) / (w1 + w2);
    }
}
