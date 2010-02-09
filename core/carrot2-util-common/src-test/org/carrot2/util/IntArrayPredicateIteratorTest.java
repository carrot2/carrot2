
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import java.util.*;

import org.junit.*;

import com.google.common.base.*;

/**
 * 
 */
public class IntArrayPredicateIteratorTest
{
    private final int SEP = -1;
    private Predicate<Integer> equalsSep = Predicates.equalTo(SEP);

    @Test
    public void testSimpleCase() {
        compare(
            new int [] {
                0, SEP,   0, 1, SEP, 0
            },
            new int [] {
                0, 1,     2, 2,   5, 1
            });
    }

    @Test
    public void testBorderCase() {
        compare(
            new int [] {
                SEP, SEP, 1, SEP
            },
            new int [] {
                0, 0,   1, 0,   2, 1,   4, 0
            });
    }

    @Test
    public void testEmptyArray() {
        compare(
            new int [] {
            },
            new int [] {
            });
    }

    @Test
    public void testNoSeparator() {
        compare(
            new int [] {
                0, 0, 0, 0
            },
            new int [] {
                0, 4
            });
    }

    @Test
    public void testSubrange() {
        compare(
            new int [] {
                SEP, 0, 1, 2, SEP, 3
            },
            1, 3,
            new int [] {
                1, 3
            });

        compare(
            new int [] {
                SEP, 0, 1, 2, SEP, 3
            },
            2, 3,
            new int [] {
                2, 2,
                5, 0,
            });
    }
    
    /*
     * 
     */
    private void compare(int [] data, int [] expectedValues)
    {
        compare(data, 0, data.length, expectedValues);
    }

    /*
     * 
     */
    private void compare(int [] data, int from, int length, int [] expectedValues)
    {
        final ArrayList<Integer> results = new ArrayList<Integer>();
        final IntArrayPredicateIterator i = new IntArrayPredicateIterator(data, from, length, equalsSep);
        while (i.hasNext())
        {
            results.add(i.next());
            results.add(i.getLength());
        }
        
        final ArrayList<Integer> expected = new ArrayList<Integer>();
        for (int j : expectedValues) expected.add(j);

        Assert.assertEquals(expected, results);
    }
}
