
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

import java.util.ArrayList;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Assert;
import org.junit.Test;

import com.carrotsearch.hppc.predicates.ShortPredicate;

/**
 * 
 */
public class IntArrayPredicateIteratorTest extends CarrotTestCase
{
    private final static int SEP = -1;
    private ShortPredicate equalsSep = new ShortPredicate()
    {
        
        public boolean apply(short arg)
        {
            return arg == SEP;
        }
    };

    @Test
    public void testSimpleCase() {
        compare(
            new short [] {
                0, SEP,   0, 1, SEP, 0
            },
            new short [] {
                0, 1,     2, 2,   5, 1
            });
    }

    @Test
    public void testBorderCase() {
        compare(
            new short [] {
                SEP, SEP, 1, SEP
            },
            new short [] {
                0, 0,   1, 0,   2, 1,   4, 0
            });
    }

    @Test
    public void testEmptyArray() {
        compare(
            new short [] {
            },
            new short [] {
            });
    }

    @Test
    public void testNoSeparator() {
        compare(
            new short [] {
                0, 0, 0, 0
            },
            new short [] {
                0, 4
            });
    }

    @Test
    public void testSubrange() {
        compare(
            new short [] {
                SEP, 0, 1, 2, SEP, 3
            },
            1, 3,
            new short [] {
                1, 3
            });

        compare(
            new short [] {
                SEP, 0, 1, 2, SEP, 3
            },
            2, 3,
            new short [] {
                2, 2,
                5, 0,
            });
    }
    
    /*
     * 
     */
    private void compare(short [] data, short [] expectedValues)
    {
        compare(data, 0, data.length, expectedValues);
    }

    /*
     * 
     */
    private void compare(short [] data, int from, int length, short [] expectedValues)
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
