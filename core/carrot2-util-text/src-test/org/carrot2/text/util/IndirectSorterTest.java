package org.carrot2.text.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;

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
    public void testEmpty()
    {
        final String [] array = new String [] {};
        final int [] expectedOrder = new int [] {};

        check(array, expectedOrder);
    }

    @Test
    public void testSingleElement()
    {
        final String [] array = new String []
        {
            "a"
        };
        final int [] expectedOrder = new int []
        {
            0
        };

        check(array, expectedOrder);
    }

    @Test
    public void testSorted()
    {
        final String [] array = new String []
        {
            "a", "b", "c", "d", "e", "f", "g"
        };
        final int [] expectedOrder = new int []
        {
            6, 5, 4, 3, 2, 1, 0
        };

        check(array, expectedOrder);
    }

    @Test
    public void testReversed()
    {
        final String [] array = new String []
        {
            "n", "m", "l", "k", "j", "i", "h", "g", "f", "e", "d", "c", "b", "a"
        };
        int [] expectedOrder = new int []
        {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
        };

        check(array, expectedOrder);
    }

    @Test
    public void testUnsorted()
    {
        final String [] array = new String []
        {
            "d", "e", "c", "b", "f", "g", "a"
        };
        int [] expectedOrder = new int []
        {
            5, 4, 1, 0, 2, 3, 6
        };

        check(array, expectedOrder);
    }

    @Test
    public void testRepeatedElements()
    {
        final String [] array = new String []
        {
            "d", "e", "c", "a", "f", "f", "a"
        };
        final int [] expectedOrder = new int []
        {
            4, 5, 1, 0, 2, 3, 6
        };

        check(array, expectedOrder);
    }

    private void check(String [] array, int [] expectedOrder)
    {
        int [] order = IndirectSorter.sort(array, REVERSED_STRING_COMPARATOR);
        assertThat(order).isEqualTo(expectedOrder);
    }
}
