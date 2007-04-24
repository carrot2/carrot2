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
