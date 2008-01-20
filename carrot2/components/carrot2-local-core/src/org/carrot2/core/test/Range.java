
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

package org.carrot2.core.test;

/**
 * An integer range between two values.
 * 
 * @author Dawid Weiss
 */
public final class Range
{
    private final int min;
    private final int max;

    /**
     * Exact range (inclusive).
     */
    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @return Returns <code>true</code> if <code>value</code>
     * is within this range's limits.
     */
    public boolean isIn(int value) {
        return value >= min && value <= max;
    }

    /**
     * Returns a new instance of an exact range matching
     * <code>value</code>.
     */
    public static Range exact(int value)
    {
        return new Range(value, value);
    }

    /**
     * 
     */
    public String toString() {
        return "[" + min + "," + max + "]";
    }
}
