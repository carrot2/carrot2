
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

import java.util.LinkedList;

/**
 * Calculates an average of values showing up in a given time window. To keep processing
 * efficient, bucketing is performed.
 * <p>
 * This class is not thread-safe.
 */
public class RollingWindowAverage
{
    /** Helpful constant for one millisecond. */
    public static final int MILLIS = 1;

    /** Helpful constant for one second. */
    public static final int SECOND = 1000 * MILLIS;

    /** Helpful constant for one minute. */
    public static final int MINUTE = 60 * SECOND;

    /**
     * An entry in {@link #buckets}.
     */
    private final static class Bucket
    {
        public final long deadline;

        public long elements;
        public long sum;

        public Bucket(long deadline)
        {
            this.deadline = deadline;
        }
    }

    /**
     * Each entry in this list is a partial sum of all objects that fell into the same
     * time period.
     */
    final LinkedList<Bucket> buckets = new LinkedList<Bucket>();

    /**
     * Up-to-date sum of elements in all buckets.
     */
    private long currentSum;

    /**
     * Up-to-date count of all elements in all buckets.
     */
    private long currentCount;

    private long bucketSizeMillis;
    private long windowSizeMillis;

    /**
     * 
     */
    public RollingWindowAverage(long windowSizeMillis, long bucketSizeMillis)
    {
        if (bucketSizeMillis <= 0 || windowSizeMillis <= 0
            || windowSizeMillis <= bucketSizeMillis)
        {
            throw new IllegalArgumentException("Bucket size must be smaller than window size.");
        }

        this.bucketSizeMillis = bucketSizeMillis;
        this.windowSizeMillis = windowSizeMillis;
    }

    /**
     * Adds a new entry.
     */
    public final void add(long timestamp, long value)
    {
        final long now = getNow();
        removeOldBuckets(now);

        Bucket bucket;
        if (buckets.isEmpty() || (bucket = buckets.getFirst()).deadline < timestamp)
        {
            // Create new bucket.
            bucket = new Bucket(now + bucketSizeMillis);
            buckets.addFirst(bucket);
        }

        bucket.elements++;
        currentCount++;
        bucket.sum += value;
        currentSum += value;
    }

    /**
     *
     */
    public final double getCurrentAverage()
    {
        removeOldBuckets(getNow());

        if (this.currentCount == 0)
        {
            return 0;
        }
        else
        {
            return ((double) this.currentSum) / this.currentCount;
        }
    }

    /**
     * Returns the number of updates kept in the rolling window's scope.
     */
    public final long getUpdatesInWindow()
    {
        removeOldBuckets(getNow());
        return this.currentCount;
    }

    /**
     * Returns the size of the rolling window. 
     */
    public final long getWindowSizeMillis()
    {
        return windowSizeMillis;
    }

    /**
     * Returns <code>System.currentTimeMillise()</code>, but moved to a separate method to speed
     * up JUnit tests and make them independent of actual wall time.
     */
    long getNow()
    {
        return System.currentTimeMillis();
    }

    /**
     * Remove buckets that fall outside the rolling window's scope.
     */
    private void removeOldBuckets(long now)
    {
        final long purgeTimestamp = now - this.windowSizeMillis;

        Bucket bucket;
        while (!buckets.isEmpty()
            && (bucket = buckets.getLast()).deadline < purgeTimestamp)
        {
            currentSum -= bucket.sum;
            currentCount -= bucket.elements;
            buckets.removeLast();
        }
    }
}
