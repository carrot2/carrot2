
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import bak.pcj.IntComparator;

/**
 * A number of implementations of {@link IntComparator}s.
 */
public final class IntComparators
{
    /**
     * Compares <code>int</code> in their natural order.
     */
    public static final IntComparator NATURAL_ORDER = new NaturalOrderIntComparator();

    /**
     * Compares <code>int</code> in their reversed order.
     */
    public static final IntComparator REVERSED_ORDER = new ReversedOrderIntComparator();

    /**
     * Natural int order.
     */
    private static class NaturalOrderIntComparator implements IntComparator
    {
        public int compare(int v1, int v2)
        {
            return v1 - v2;
        }
    }

    /**
     * Reversed int order.
     */
    private static class ReversedOrderIntComparator implements IntComparator
    {
        public int compare(int v1, int v2)
        {
            return v2 - v1;
        }
    }

    /**
     * No instantiation.
     */
    private IntComparators()
    {
    }
}
