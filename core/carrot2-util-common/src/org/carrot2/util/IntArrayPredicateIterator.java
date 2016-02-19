
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

import java.util.Iterator;

import com.carrotsearch.hppc.predicates.ShortPredicate;

/**
 * Iterates over <i>ranges</i> between elements for which a given predicate returns
 * <code>true</code>. The returned range may be of zero length ({@link #getLength()}).
 * <p>
 * An example probably best explains what this class does. Consider the following array:
 * 
 * <pre>
 * [SEP, SEP, 1, SEP] 
 * </pre>
 * 
 * where <code>SEP</code> is something for which the predicate returns <code>true</code>.
 * If so, then the returned subranges would be equal to (start index, length):
 * 
 * <pre>
 * [0,0]  (empty range before the first separator)
 * [1,0]  (empty range after the first separator)
 * [2,1]  ([1])
 * [4,0]  (empty range after last separator)
 * </pre>
 */
public final class IntArrayPredicateIterator implements Iterator<Integer>
{
    private ShortPredicate separator;
    private short [] array;

    private int rangeStart;
    private int rangeLength;
    
    private final int length;
    private final int toIndex;

    public IntArrayPredicateIterator(short [] array, int from, int length, ShortPredicate separator)
    {
        this.separator = separator;
        this.array = array;
        
        this.length = length;
        this.toIndex = from + length;

        rangeStart = from - 1;
    }

    public IntArrayPredicateIterator(short [] array, ShortPredicate separator)
    {
        this(array, 0, array.length, separator);
    }

    public boolean hasNext()
    {
        return length > 0 && rangeStart < toIndex;
    }

    public Integer next()
    {
        final int result = rangeStart;

        rangeStart = nextSeparator(rangeStart);
        rangeLength = rangeStart - result - 1;

        return result + 1;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the length (number of elements) of the range most recently acquired from
     * {@link #next()}.
     */
    public int getLength()
    {
        return rangeLength;
    }

    /*
     * 
     */
    private int nextSeparator(int position)
    {
        do
        {
            position++;
        }
        while (position < toIndex && !separator.apply(array[position]));

        return position;
    }
}
