
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

package org.carrot2.util.collect.primitive;

/**
 * A simple queue-like list of primitive integers. Supports adding and removal of int
 * values at the the end of the queue and random access to any element (including the
 * underlying int array).
 * <p>
 * Does <b>not</b> follow Java Collections semantics. Index range validity is only
 * verified when running with assertions (<code>-ea</code> when starting the JVM).
 */
public final class IntQueue
{
    /** An empty storage buffer. */
    private final static int [] EMPTY_BUFFER = new int [0];

    /** Minimum grow count (10 elements). */
    public final static int DEFAULT_MIN_GROW_COUNT = 10;

    /** Maximum grow count (approximately 1MB of memory). */
    public final static int DEFAULT_MAX_GROW_COUNT = 1024 * 1024 / 4;

    /** Minimum number of elements to grow, if capacity exceeded. */
    private final int minGrowCount;

    /** Maximum number of elements to grow, if capacity exceeded. */
    private final int maxGrowCount;

    /**
     * Internal array for storing queue values. The array may be larger than the current
     * size of the queue ({@link #size()}).
     */
    public int [] buffer = EMPTY_BUFFER;

    /**
     * Current number of elements stored in {@link #buffer}.
     */
    public int elementsCount = 0;

    /**
     * Create with default grow counts.
     */
    public IntQueue()
    {
        this(DEFAULT_MIN_GROW_COUNT, DEFAULT_MAX_GROW_COUNT);
    }

    /**
     * Create with the desired initial capacity.
     */
    public IntQueue(int initialCapacity)
    {
        this();
        ensureSize(initialCapacity);
    }

    /**
     * Create with minimum and maximum grow counts. The internal array will double its
     * size when reallocating, but only within the provided limits.
     * 
     * @param minGrow Minimum reallocation size.
     * @param maxGrow Maximum reallocation size.
     */
    public IntQueue(int minGrow, int maxGrow)
    {
        assert minGrow <= maxGrow && minGrow > 0;

        this.minGrowCount = minGrow;
        this.maxGrowCount = maxGrow;
    }

    /**
     * Adds an integer to the end of the list.
     */
    public final void push(int e1)
    {
        ensureSize(1);
        buffer[elementsCount++] = e1;
    }

    /**
     * Adds two integers to the end of the list.
     */
    public final void push(int e1, int e2)
    {
        ensureSize(2);
        buffer[elementsCount++] = e1;
        buffer[elementsCount++] = e2;
    }

    /**
     * Adds three integers to the end of the list.
     */
    public final void push(int e1, int e2, int e3)
    {
        ensureSize(3);
        buffer[elementsCount++] = e1;
        buffer[elementsCount++] = e2;
        buffer[elementsCount++] = e3;
    }

    /**
     * Vararg version.
     */
    public final void push(int... elements)
    {
        push(elements, 0, elements.length);
    }

    /**
     * Arrays for longer lists of arguments.
     */
    public final void push(int [] elements, int start, int len)
    {
        ensureSize(len);
        System.arraycopy(elements, start, buffer, elementsCount, len);
        elementsCount += len;
    }

    /**
     * Remove an arbitrary number of integers from the end of the list.
     */
    public final void pop(int count)
    {
        elementsCount -= count;
    }

    /**
     * Remove the last element from the list.
     */
    public final void pop()
    {
        elementsCount--;
    }

    /**
     * Remove the last element from the list and return it.
     */
    public final int popGet()
    {
        return buffer[--elementsCount];
    }

    /**
     * Increases internal buffer size if needed.
     */
    private final void ensureSize(int expectedAdditions)
    {
        if (elementsCount + expectedAdditions >= buffer.length)
        {
            /*
             * Grow by half the current size by default, but keep the limits.
             */
            int growBy = buffer.length / 2 + 1;
            growBy = Math.max(minGrowCount, growBy);
            growBy = Math.min(maxGrowCount, growBy);
            growBy = Math.max(elementsCount + expectedAdditions, growBy);

            final int [] newBuffer = new int [buffer.length + growBy];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            this.buffer = newBuffer;
        }
    }

    /**
     * @return The number of currently stored elements.
     */
    public final int size()
    {
        return elementsCount;
    }

    /**
     * @return Returns an element at index <code>index</code>.
     */
    public final int get(int index)
    {
        assert index >= 0 && index < elementsCount 
            : "index oob: " + index + " (" + elementsCount + ")";

        return buffer[index];
    }

    /**
     * Sets the number of stored elements to zero (but does not release or clear the
     * internal storage array).
     */
    public void clear()
    {
        this.elementsCount = 0;
    }

    /**
     * Sets the number of stored elements to zero and releases the internal storage array.
     */
    public void release()
    {
        clear();
        this.buffer = EMPTY_BUFFER;
    }

    /**
     * Create a copy of the queue's elements.
     */
    public int [] toArray()
    {
        final int [] cloned = new int [size()];
        System.arraycopy(buffer, 0, cloned, 0, size());
        return cloned;
    }
}
