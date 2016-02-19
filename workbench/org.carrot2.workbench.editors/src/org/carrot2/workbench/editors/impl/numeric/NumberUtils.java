
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

package org.carrot2.workbench.editors.impl.numeric;

import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.util.Locale;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * Some detached utility methods related to numeric annotations.
 */
final class NumberUtils
{
    private NumberUtils()
    {
        // no instances.
    }

    /**
     * @return Return <code>true</code> if one of the <code>range's</code> ends
     * is infinite or reaches maximum/minimum value.
     */
    public static boolean isUnbounded(DoubleRange range)
    {
        return Double.isInfinite(range.min())
            || Double.isInfinite(range.max())
            || Double.MIN_VALUE == range.min()
            || Double.MAX_VALUE == range.max();
    }

    /**
     * @return Return <code>true</code> if one of the <code>range's</code> ends
     * reaches maximum/minimum value.
     */
    public static boolean isUnbounded(IntRange range)
    {
        return Integer.MIN_VALUE == range.min()
            || Integer.MAX_VALUE == range.max();
    }

    /**
     * @return {@link DoubleRange} annotation for a given {@link AttributeDescriptor}. If one does
     * not exist, <code>null</code> is returned.
     */
    public static DoubleRange getDoubleRange(AttributeDescriptor descriptor)
    {
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof DoubleRange)
            {
                return (DoubleRange) ann;
            }
        }
        
        return null;
    }
    
    /**
     * @return {@link IntRange} annotation for a given {@link AttributeDescriptor}. If one does
     * not exist, <code>null</code> is returned.
     */
    public static IntRange getIntRange(AttributeDescriptor descriptor)
    {
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof IntRange)
            {
                return (IntRange) ann;
            }
        }
        
        return null;
    }

    /**
     * 
     */
    public static String getTooltip(int min, int max)
    {
        final boolean minBounded = (min == Integer.MIN_VALUE);
        final boolean maxBounded = (max == Integer.MAX_VALUE);

        if (!minBounded && !maxBounded)
        {
            return "Valid range: unbounded integer";
        }
        else
        {
            return "Valid range: [" + to_s(min) + "; " + to_s(max) + "]";
        }
    }

    /**
     * 
     */
    public static String getTooltip(double min, double max)
    {
        final boolean minBounded = (Double.isInfinite(min) || min == Double.MIN_VALUE);
        final boolean maxBounded = (Double.isInfinite(max) || max == Double.MAX_VALUE);

        if (!minBounded && !maxBounded)
        {
            return "Valid range: unbounded";
        }
        else
        {
            return "Valid range: [" + to_s(min) + "; " + to_s(max) + "]";
        }
    }

    /**
     * Converts the given bound to a human-readable string.
     */
    public static String to_s(int v)
    {
        if (v == Integer.MIN_VALUE)
        {
            return "-\u221E";
        }
        else if (v == Integer.MAX_VALUE)
        {
            return "\u221E";
        }
        else
        {
            return Integer.toString(v);
        }
    }
    
    /**
     * Converts the given bound to a human-readable string.
     */
    public static String to_s(double v)
    {
        if (v == Double.MIN_VALUE || v == Double.NEGATIVE_INFINITY)
        {
            return "-\u221E";
        }
        else if (v == Double.MAX_VALUE || v == Double.POSITIVE_INFINITY)
        {
            return "\u221E";
        }
        else
        {
            return to_decimal(v);
        }
    }

    /**
     * Returns a decimal representation of a double 
     * (no scientific notation used).
     */
    public static String to_decimal(double v)
    {
        final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(4);
        return nf.format(v);
    }
}
