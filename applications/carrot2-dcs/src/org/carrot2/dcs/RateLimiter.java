package org.carrot2.dcs;

import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * A simple keyed event rate limiter based the Token Bucket algorithm. A typical use case
 * of this utility is limiting the number of requests coming from one IP address along the
 * pattern of: no more than 2 requests per second, no more than 30 per minute and no more
 * than 1000 per day.
 * 
 * @param <T> type of the key
 */
public class RateLimiter<T>
{
    /**
     * Ordered list of limits.
     */
    private final List<RateLimit> limits;

    /**
     * A cache of buckets for each key.
     */
    private final LoadingCache<T, TokenBucket> buckets = CacheBuilder.newBuilder().build(
        new CacheLoader<T, TokenBucket>()
        {
            public TokenBucket load(T key)
            {
                return new TokenBucket(limits);
            }
        });

    /**
     * Constructed using the builder pattern.
     */
    private RateLimiter(List<RateLimit> limits)
    {
        this.limits = limits;
    }

    /**
     * Initialize the limiter builder.
     */
    public static <T> RateLimiterBuilder<T> create()
    {
        return new RateLimiterBuilder<T>();
    }

    /**
     * Returns <code>true</code> if the event associated with <code>key</code> meets all
     * limits. If any of the limits is exceeded, returns <code>false</code>.
     */
    public boolean check(T key)
    {
        return buckets.getUnchecked(key).check();
    }

    /**
     * Builder of {@link RateLimiter} instances.
     * 
     * @param <T> type of the rate limiter's key
     */
    public static final class RateLimiterBuilder<T>
    {
        private final List<RateLimit> limits = Lists.newArrayList();

        /**
         * Adds a limit of <code>allowed</code> events per <code>periodMilliseconds</code>.
         * Limits must be added in the strictly increasing order of period lengths and in
         * the strictly decreasing order of rates (
         * <code>allowed / periodMilliseconds</code>).
         */
        public RateLimiterBuilder<T> limitTo(int allowed, int periodMilliseconds)
        {
            // Check for argument validity. Don't bother sorting, fail fast.
            if (!limits.isEmpty())
            {
                final int lastPeriod = limits.get(limits.size() - 1).period;
                if (periodMilliseconds <= lastPeriod)
                {
                    throw new IllegalArgumentException(
                        "Subsequent limiting periods must be strictly increasing.");
                }

                final int lastAllowed = limits.get(limits.size() - 1).allowed;
                if (allowed / (double) periodMilliseconds >= lastAllowed
                    / (double) lastPeriod)
                {
                    throw new IllegalArgumentException(
                        "Subsequent allowed rates (allowed / period) must be strictly decreasing.");
                }

            }
            limits.add(new RateLimit(allowed, periodMilliseconds));
            return this;
        }

        /**
         * Returns the {@link RateLimiter} instance.
         */
        public RateLimiter<T> build()
        {
            return new RateLimiter<T>(limits);
        }
    }

    /**
     * Represents an individual rate limit.
     */
    private static final class RateLimit
    {
        final int allowed;
        final int period;

        RateLimit(int allowed, int periodMilliseconds)
        {
            this.allowed = allowed;
            this.period = periodMilliseconds;
        }
    }

    /**
     * A hierarchical extension of the Token Bucket algorithm.
     */
    private static final class TokenBucket
    {
        private final List<RateLimit> limits;
        private float [] currentAllowed;
        private long lastCheck;

        TokenBucket(List<RateLimit> limits)
        {
            this.limits = limits;
            this.lastCheck = System.currentTimeMillis();
            this.currentAllowed = new float [limits.size()];
            for (int i = 0; i < currentAllowed.length; i++)
            {
                currentAllowed[i] = limits.get(i).allowed;
            }
        }

        synchronized boolean check()
        {
            final long now = System.currentTimeMillis();
            final long passed = now - lastCheck;
            lastCheck = now;

            boolean shouldAllow = true;
            for (int i = 0; i < currentAllowed.length; i++)
            {
                final int limitAllowed = limits.get(i).allowed;
                final int limitPeriod = limits.get(i).period;

                currentAllowed[i] = Math.min(limitAllowed, currentAllowed[i] + passed
                    * (limitAllowed / (float) limitPeriod));

                if (currentAllowed[i] < 1)
                {
                    shouldAllow = false;
                }
                else
                {
                    if (shouldAllow)
                    {
                        currentAllowed[i] -= 1;
                    }
                }
            }

            return shouldAllow;
        }
    }
}
