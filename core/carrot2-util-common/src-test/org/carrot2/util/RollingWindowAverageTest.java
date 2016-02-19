
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

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link RollingWindowAverage}.
 */
public class RollingWindowAverageTest extends CarrotTestCase
{
    long now;

    private class RollingWindowAverageFakeTime extends RollingWindowAverage
    {
        public RollingWindowAverageFakeTime(long windowSizeMillis, long bucketSizeMillis)
        {
            super(windowSizeMillis, bucketSizeMillis);
        }

        long getNow()
        {
            return now;
        }
    }

    @Before
    public void setup()
    {
        now = System.currentTimeMillis();
    }

    @Test
    public void testRollingAverage() throws InterruptedException
    {
        final RollingWindowAverageFakeTime rwa = new RollingWindowAverageFakeTime(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        rwa.add(now, 2);
        rwa.add(now, 3);
        rwa.add(now, 5);

        assertEquals("Average incorrect.", (2.0 + 3 + 5) / 3, rwa.getCurrentAverage(),
            0.01d);

        sleep(120 * RollingWindowAverage.MILLIS);

        rwa.add(now, 7);

        assertEquals("Average incorrect.", (2.0 + 3 + 5 + 7) / 4,
            rwa.getCurrentAverage(), 0.01d);

        sleep(500 * RollingWindowAverage.MILLIS);

        rwa.add(now, 9);

        assertEquals("Average incorrect.", (2.0 + 3 + 5 + 7 + 9) / 5, rwa
            .getCurrentAverage(), 0.01d);

        sleep(1200 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    @Test
    public void testEmpty() throws InterruptedException
    {
        final RollingWindowAverageFakeTime rwa = new RollingWindowAverageFakeTime(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    @Test
    public void testBucketsNumber() throws InterruptedException
    {
        final RollingWindowAverageFakeTime rwa = new RollingWindowAverageFakeTime(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        rwa.add(now, 2);
        rwa.add(now, 3);
        rwa.add(now, 5);

        assertEquals(1, rwa.buckets.size());

        sleep(120 * RollingWindowAverage.MILLIS);

        rwa.add(now, 7);

        assertEquals(2, rwa.buckets.size());

        sleep(3000 * RollingWindowAverage.MILLIS);

        rwa.getCurrentAverage();
        assertEquals(0, rwa.buckets.size());
    }

    /**
     * Fake thread sleep (adds to time counter only).
     */
    private void sleep(int delay)
    {
        this.now += delay;
    }
}
