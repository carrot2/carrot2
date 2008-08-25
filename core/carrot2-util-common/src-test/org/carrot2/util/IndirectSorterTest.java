package org.carrot2.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;

import bak.pcj.DoubleComparator;
import bak.pcj.IntComparator;

import com.google.common.collect.Comparators;

/**
 * Test cases for {@link IndirectSorter}.
 */
public class IndirectSorterTest
{
    /** Test comparator */
    private static final Comparator<String> REVERSED_STRING_COMPARATOR = Collections
        .<String> reverseOrder(Comparators.<String> naturalOrder());

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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, IntComparators.REVERSED_ORDER);
    }

    @Test
    public void testDoubleEmpty()
    {
        final double [] array = new double [] {};
        final int [] expectedOrder = new int [] {};

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
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

        check(array, expectedOrder, DoubleComparators.REVERSED_ORDER);
    }

    private <T> void check(T [] array, int [] expectedOrder, Comparator<T> comparator)
    {
        int [] order = IndirectSorter.sort(array, comparator);
        assertThat(order).isEqualTo(expectedOrder);
    }

    private <T> void check(int [] array, int [] expectedOrder, IntComparator comparator)
    {
        int [] order = IndirectSorter.sort(array, comparator);
        assertThat(order).isEqualTo(expectedOrder);
    }

    private <T> void check(double [] array, int [] expectedOrder,
        DoubleComparator comparator)
    {
        int [] order = IndirectSorter.sort(array, comparator);
        assertThat(order).isEqualTo(expectedOrder);
    }
}
