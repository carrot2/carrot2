
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

import java.util.Comparator;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import com.carrotsearch.hppc.sorting.IndirectComparator;
import com.carrotsearch.hppc.sorting.IndirectSort;
import org.carrot2.shaded.guava.common.collect.Ordering;

/**
 * Test cases for the legacy (removed) <code>IndirectSorter<code>. The replacement class
 * {@link IndirectSort} from HPPC should provide the same functionality.
 */
public class IndirectSorterTest extends CarrotTestCase
{
    /** Test comparator */
    private static final Comparator<String> REVERSED_STRING_COMPARATOR = Ordering
        .natural().reverse();

    @Test
    public void testObjectEmpty()
    {
        final String [] array = new String [] {};
        final int [] expectedOrder = new int [] {};

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectSingleElement()
    {
        final String [] array = new String []
        {
            "a"
        };
        final int [] expectedOrder = new int []
        {
            0
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectSorted()
    {
        final String [] array = new String []
        {
            "a", "b", "c", "d", "e", "f", "g"
        };
        final int [] expectedOrder = new int []
        {
            6, 5, 4, 3, 2, 1, 0
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectReversed()
    {
        final String [] array = new String []
        {
            "n", "m", "l", "k", "j", "i", "h", "g", "f", "e", "d", "c", "b", "a"
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectUnsorted()
    {
        final String [] array = new String []
        {
            "d", "e", "c", "b", "f", "g", "a"
        };
        int [] expectedOrder = new int []
        {
            5, 4, 1, 0, 2, 3, 6
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectRepeatedElements()
    {
        final String [] array = new String []
        {
            "d", "e", "c", "a", "f", "f", "a"
        };
        final int [] expectedOrder = new int []
        {
            4, 5, 1, 0, 2, 3, 6
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testObjectLargerArray()
    {
        final String [] array = new String []
        {
            "17", "16", "15", "14", "13", "12", "11", "10", "09", "08", "07", "06", "05",
            "04", "03", "02", "01", "00"
        };
        final int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17
        };

        check(array, expectedOrder, REVERSED_STRING_COMPARATOR);
    }

    @Test
    public void testIntEmpty()
    {
        final int [] array = new int [] {};
        final int [] expectedOrder = new int [] {};

        check(new IndirectComparator.AscendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntSingleElement()
    {
        final int [] array = new int []
        {
            2
        };
        final int [] expectedOrder = new int []
        {
            0
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntSorted()
    {
        final int [] array = new int []
        {
            2, 4, 6, 7, 8, 9, 12
        };
        final int [] expectedOrder = new int []
        {
            6, 5, 4, 3, 2, 1, 0
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntReversed()
    {
        final int [] array = new int []
        {
            21, 19, 18, 15, 14, 13, 12, 9, 8, 6, 5, 4, 3, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntUnsorted()
    {
        final int [] array = new int []
        {
            6, 8, 0, -3, 10, 13, -21
        };
        int [] expectedOrder = new int []
        {
            5, 4, 1, 0, 2, 3, 6
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntLargerArray()
    {
        final int [] array = new int []
        {
            17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testIntRepeatedElements()
    {
        final int [] array = new int []
        {
            8, 9, 4, 1, 10, 10, 1
        };
        final int [] expectedOrder = new int []
        {
            4, 5, 1, 0, 2, 3, 6
        };

        check(new IndirectComparator.DescendingIntComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleEmpty()
    {
        final double [] array = new double [] {};
        final int [] expectedOrder = new int [] {};

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleSingleElement()
    {
        final double [] array = new double []
        {
            2
        };
        final int [] expectedOrder = new int []
        {
            0
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleSorted()
    {
        final double [] array = new double []
        {
            2, 4, 6, 7, 8, 9, 12
        };
        final int [] expectedOrder = new int []
        {
            6, 5, 4, 3, 2, 1, 0
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleReversed()
    {
        final double [] array = new double []
        {
            21, 19, 18, 15, 14, 13, 12, 9, 8, 6, 5, 4, 3, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testFloatReversed()
    {
        final float [] array = new float []
        {
            21, 19, 18, 15, 14, 13, 12, 9, 8, 6, 5, 4, 3, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(new IndirectComparator.DescendingFloatComparator(array), expectedOrder);
    }

    @Test
    public void testShortReversed()
    {
        final short [] array = new short []
        {
            21, 19, 18, 15, 14, 13, 12, 9, 8, 6, 5, 4, 3, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(new IndirectComparator.DescendingShortComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleUnsorted()
    {
        final double [] array = new double []
        {
            6, 8, 0, -3, 10, 13, -21
        };
        int [] expectedOrder = new int []
        {
            5, 4, 1, 0, 2, 3, 6
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleRepeatedElements()
    {
        final double [] array = new double []
        {
            8, 9, 4, 1, 10, 10, 1
        };
        final int [] expectedOrder = new int []
        {
            4, 5, 1, 0, 2, 3, 6
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    @Test
    public void testDoubleLargerArray()
    {
        final double [] array = new double []
        {
            17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17
        };

        check(new IndirectComparator.DescendingDoubleComparator(array), expectedOrder);
    }

    private void check(int start, int length, IndirectComparator comparator,
        int [] expectedOrder)
    {
        int [] order = IndirectSort.mergesort(start, length, comparator);
        assertThat(order).isEqualTo(expectedOrder);
    }

    private void check(IndirectComparator comparator, int [] expectedOrder)
    {
        check(0, expectedOrder.length, comparator, expectedOrder);
    }

    private <T> void check(T [] input, int start, int length, Comparator<T> comparator,
        int [] expectedOrder)
    {
        int [] order = IndirectSort.mergesort(input, start, length, comparator);
        assertThat(order).isEqualTo(expectedOrder);
    }

    private <T> void check(T [] input, int [] expectedOrder, Comparator<T> comparator)
    {
        check(input, 0, input.length, comparator, expectedOrder);
    }
}
