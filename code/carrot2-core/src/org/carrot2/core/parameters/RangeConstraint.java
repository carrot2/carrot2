/**
 * 
 */
package org.carrot2.core.parameters;

import org.carrot2.util.ObjectUtils;

/**
 *
 */
public class RangeConstraint<T extends Number & Comparable<T>> implements Constraint<T>
{
    /** Minimum value, inclusive */
    private final T minValue;

    /** Maximum value, inclusive */
    private final T maxValue;

    /**
     * 
     */
    public RangeConstraint(T minValue, T maxValue)
    {
        if (minValue == null && maxValue == null)
        {
            throw new IllegalArgumentException(
                "At least one of the range ends must not be null");
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public <V extends T> boolean isMet(V value)
    {
        return value.compareTo(minValue) >= 0 && value.compareTo(maxValue) <= 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof RangeConstraint))
        {
            return false;
        }

        RangeConstraint<T> other = (RangeConstraint<T>) obj;

        return ObjectUtils.equals(this.minValue, other.minValue)
            && ObjectUtils.equals(this.maxValue, other.maxValue);
    }
    
    @Override
    public int hashCode()
    {
        int minHash = 0;
        if (minValue != null)
        {
            minHash = minValue.hashCode();
        }
        
        int  maxHash = 0;
        if (maxValue != null)
        {
            maxHash = maxValue.hashCode();
        }
        
        return minHash ^ maxHash;
    }
}
