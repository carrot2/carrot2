package org.carrot2.util;

import bak.pcj.DoubleComparator;

/**
 * A number of implementations of {@link DoubleComparator}s.
 */
public final class DoubleComparators
{
    /**
     * Compares <code>int</code> in their natural order.
     */
    public static final DoubleComparator NATURAL_ORDER = new NaturalOrderDoubleComparator();

    /**
     * Compares <code>int</code> in their reversed order.
     */
    public static final DoubleComparator REVERSED_ORDER = new ReversedOrderDoubleComparator();

    /**
     * Natural int order.
     */
    private static class NaturalOrderDoubleComparator implements DoubleComparator
    {
        public int compare(double v1, double v2)
        {
            if (v1 > v2)
            {
                return 1;
            }
            else if (v1 < v2)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Reversed int order.
     */
    private static class ReversedOrderDoubleComparator implements DoubleComparator
    {
        public int compare(double v1, double v2)
        {
            return -DoubleComparators.NATURAL_ORDER.compare(v1, v2);
        }
    }

    /**
     * No instantiation.
     */
    private DoubleComparators()
    {
    }
}
