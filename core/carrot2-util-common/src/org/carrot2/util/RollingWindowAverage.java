package org.carrot2.util;

import java.util.LinkedList;

/**
 * Calculates an average of values showing up in a given time window. To keep processing
 * efficient, bucketing is performed.
 * <p>
 * This class is not thread-safe.
 */
public final class RollingWindowAverage
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
            throw new IllegalArgumentException();
        }

        this.bucketSizeMillis = bucketSizeMillis;
        this.windowSizeMillis = windowSizeMillis;
    }

    /**
     * Adds a new entry.
     */
    public void add(long timestamp, long value)
    {
        final long now = System.currentTimeMillis();
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
    public double getCurrentAverage()
    {
        removeOldBuckets(System.currentTimeMillis());

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
     * 
     */
    public long getUpdatesInWindow()
    {
        removeOldBuckets(System.currentTimeMillis());
        return this.currentCount;
    }

    /**
     * 
     */
    public long getWindowSizeMillis()
    {
        return windowSizeMillis;
    }
    
    /**
     *
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
