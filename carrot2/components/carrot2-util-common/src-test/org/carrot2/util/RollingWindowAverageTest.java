/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;

import junit.framework.TestCase;

/**
 * Tests {@link RollingWindowAverage}.
 * 
 * @author Dawid Weiss
 */
public class RollingWindowAverageTest extends TestCase
{
    /**
     *
     */
    public RollingWindowAverageTest(String testName)
    {
        super(testName);
    }

    /**
     *
     */
    public void testRollingAverage() throws InterruptedException
    {
        final RollingWindowAverage rwa = new RollingWindowAverage(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 2);
        rwa.add(System.currentTimeMillis(), 3);
        rwa.add(System.currentTimeMillis(), 5);

        assertEquals("Average incorrect.", (2.0 + 3 + 5) / 3, rwa.getCurrentAverage(),
            0.01d);

        Thread.sleep(120 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 7);

        assertEquals("Average incorrect.", (2.0 + 3 + 5 + 7) / 4,
            rwa.getCurrentAverage(), 0.01d);

        Thread.sleep(500 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 9);

        assertEquals("Average incorrect.", (2.0 + 3 + 5 + 7 + 9) / 5, rwa
            .getCurrentAverage(), 0.01d);

        Thread.sleep(1100 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    /**
     *
     */
    public void testEmpty() throws InterruptedException
    {
        final RollingWindowAverage rwa = new RollingWindowAverage(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    /**
     *
     */
    public void testBucketsNumber() throws InterruptedException
    {
        final RollingWindowAverage rwa = new RollingWindowAverage(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 2);
        rwa.add(System.currentTimeMillis(), 3);
        rwa.add(System.currentTimeMillis(), 5);

        assertEquals(1, rwa.buckets.size());

        Thread.sleep(120 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 7);

        assertEquals(2, rwa.buckets.size());

        Thread.sleep(500 * RollingWindowAverage.MILLIS);

        rwa.add(System.currentTimeMillis(), 9);

        assertEquals(3, rwa.buckets.size());

        Thread.sleep(1100 * RollingWindowAverage.MILLIS);

        rwa.getCurrentAverage();
        assertEquals(0, rwa.buckets.size());
    }
}
