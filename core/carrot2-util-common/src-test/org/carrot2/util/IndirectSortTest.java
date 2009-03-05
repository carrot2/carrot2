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

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link IndirectSort}.
 */
public class IndirectSortTest
{
    /**
     * Implies the same order as the order of indices.
     */
    private static class OrderedInputComparator implements IndirectComparator
    {
        @Override
        public int compare(int a, int b)
        {
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        }
    }

    /**
     * Implies reverse order of indices.
     */
    private static class ReverseOrderedInputComparator extends OrderedInputComparator
    {
        @Override
        public int compare(int a, int b)
        {
            return -super.compare(a, b);
        }
    }

    enum DataDistribution
    {
        ordered, sawtooth, rand, stagger, plateau, shuffle
    }

    /**
     * Test "certification" program as in Bentley and McIlroy's paper.
     */
    @Test
    public void testSortCertification()
    {
        int [] n_values =
        {
            100, 1023, 1024, 1025, 1024 * 32
        };
        Random rand = new Random();
        for (int n : n_values)
        {
            for (int m = 1; m < 2 * n; m *= 2)
            {
                for (DataDistribution dist : DataDistribution.values())
                {
                    int [] x = new int [n];
                    for (int i = 0, j = 0, k = 1; i < n; i++)
                    {
                        switch (dist)
                        {
                            case ordered:
                                x[i] = i;
                                break;
                            case sawtooth:
                                x[i] = i % m;
                                break;
                            case rand:
                                x[i] = rand.nextInt() % m;
                                break;
                            case stagger:
                                x[i] = (i * m + i) % n;
                                break;
                            case plateau:
                                x[i] = Math.min(i, m);
                                break;
                            case shuffle:
                                x[i] = (rand.nextInt() % m) != 0 ? (j += 2) : (k += 2);
                                break;
                            default:
                                throw new RuntimeException();
                        }
                    }

                    String testName = dist + "-" + n + "-" + m;
                    testOn(copy(x), testName + "-normal");
                    testOn(reverse(x, 0, n), testName + "-reversed");
                    testOn(reverse(x, 0, n/2), testName + "-reversed_front");
                    testOn(reverse(x, n/2, n), testName + "-reversed_back");
                    testOn(sort(x), testName + "-sorted");
                    testOn(dither(x), testName + "-dither");
                }
            }
        }
    }

    private static int [] sort(int [] x)
    {
        x = copy(x);
        Arrays.sort(x);
        return x;
    }    

    private static int [] dither(int [] x)
    {
        x = copy(x);
        for (int i = 0; i < x.length; i++) x[i] += i % 5;
        return x;
    }    

    private static int [] reverse(int [] x, int start, int end)
    {
        x = copy(x);
        for (int i = start, j = end - 1; i < j; i++, j--)
        {
            int v = x[i];
            x[i] = x[j];
            x[j] = v;
        }
        return x;
    }

    private static int [] copy(int [] x)
    {
        return (int []) x.clone();
    }

    @SuppressWarnings("unused")
    private static void testOn(int [] x, String testName)
    {
        final long start = System.currentTimeMillis();
        final int [] order;
        final IndirectComparator c = new IndirectComparator.AscendingIntComparator(x);
        try
        {
            order = IndirectSort.sort(0, x.length, c);
        }
        finally
        {
            // System.out.println(testName + ": " + (System.currentTimeMillis() - start));
        }

        assertOrder(order, x.length, c);
    }

    /**
     * Data length for certain tests.
     */
    final int length = 1000000;

    /**
     * Large ordered input.
     */
    @Test
    public void testOrdered()
    {
        final IndirectComparator comparator = new OrderedInputComparator();
        final int [] order = IndirectSort.sort(0, length, comparator);
        assertOrder(order, length, comparator);
    }

    /**
     * Large reversed input.
     */
    @Test
    public void testReversed()
    {
        final IndirectComparator comparator = new ReverseOrderedInputComparator();
        final int [] order = IndirectSort.sort(0, length, comparator);
        assertOrder(order, length, comparator);
    }

    /*
     * 
     */
    private static void assertOrder(final int [] order, int length,
        final IndirectComparator comparator)
    {
        for (int i = 1; i < length; i++)
        {
            Assert.assertTrue(comparator.compare(order[i - 1], order[i]) <= 0);
        }
    }

    /**
     * Randomized input, ascending int comparator.
     */
    @Test
    public void testAscInt()
    {
        final int maxSize = 500;
        final int rounds = 1000;
        final int vocabulary = 10;
        final Random rnd = new Random(0x11223344);

        for (int round = 0; round < rounds; round++)
        {
            final int [] input = generateRandom(maxSize, vocabulary, rnd);

            final IndirectComparator comparator = new IndirectComparator.AscendingIntComparator(
                input);

            final int start = rnd.nextInt(input.length - 1);
            final int length = (input.length - start);
            final int [] order = IndirectSort.sort(start, length, comparator);

            assertOrder(order, length, comparator);
        }
    }

    /**
     * Randomized input, descending int comparator.
     */
    @Test
    public void testDescInt()
    {
        final int maxSize = 500;
        final int rounds = 1000;
        final int vocabulary = 10;
        final Random rnd = new Random(0x11223344);

        for (int round = 0; round < rounds; round++)
        {
            final int [] input = generateRandom(maxSize, vocabulary, rnd);

            final IndirectComparator comparator = new IndirectComparator.DescendingIntComparator(
                input);

            final int start = rnd.nextInt(input.length - 1);
            final int length = (input.length - start);
            final int [] order = IndirectSort.sort(start, length, comparator);

            assertOrder(order, length, comparator);
        }
    }

    /**
     * Randomized input, ascending double comparator.
     */
    @Test
    public void testAscDouble()
    {
        final int maxSize = 1000;
        final int rounds = 1000;
        final Random rnd = new Random(0x11223344);

        for (int round = 0; round < rounds; round++)
        {
            final double [] input = generateRandom(maxSize, rnd);

            final IndirectComparator comparator = new IndirectComparator.AscendingDoubleComparator(
                input);

            final int start = rnd.nextInt(input.length - 1);
            final int length = (input.length - start);
            final int [] order = IndirectSort.sort(start, length, comparator);

            assertOrder(order, length, comparator);
        }
    }

    /*
     * 
     */
    private int [] generateRandom(final int maxSize, final int vocabulary,
        final Random rnd)
    {
        final int [] input = new int [2 + rnd.nextInt(maxSize)];
        for (int i = 0; i < input.length; i++)
        {
            input[i] = vocabulary / 2 - rnd.nextInt(vocabulary);
        }
        return input;
    }

    /*
     * 
     */
    private double [] generateRandom(final int maxSize, final Random rnd)
    {
        final double [] input = new double [2 + rnd.nextInt(maxSize)];
        for (int i = 0; i < input.length; i++)
        {
            input[i] = rnd.nextGaussian();
        }
        return input;
    }
}
