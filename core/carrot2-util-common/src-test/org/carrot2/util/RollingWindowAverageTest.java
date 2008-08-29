package org.carrot2.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link RollingWindowAverage}.
 */
public class RollingWindowAverageTest
{
    @Test
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

        Thread.sleep(1200 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    @Test
    public void testEmpty() throws InterruptedException
    {
        final RollingWindowAverage rwa = new RollingWindowAverage(
            1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

        assertEquals("Average incorrect.", 0, rwa.getCurrentAverage(), 0.01d);
    }

    @Test
    public void testBucketsNumber() throws InterruptedException
    {
        final int previousPriority = Thread.currentThread().getPriority();
        try
        {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            final RollingWindowAverage rwa = new RollingWindowAverage(
                1 * RollingWindowAverage.SECOND, 100 * RollingWindowAverage.MILLIS);

            rwa.add(System.currentTimeMillis(), 2);
            rwa.add(System.currentTimeMillis(), 3);
            rwa.add(System.currentTimeMillis(), 5);

            assertEquals(1, rwa.buckets.size());

            Thread.sleep(120 * RollingWindowAverage.MILLIS);

            rwa.add(System.currentTimeMillis(), 7);

            assertEquals(2, rwa.buckets.size());

            Thread.sleep(3000 * RollingWindowAverage.MILLIS);

            rwa.getCurrentAverage();
            assertEquals(0, rwa.buckets.size());
        }
        finally
        {
            Thread.currentThread().setPriority(previousPriority);
        }
    }
}
