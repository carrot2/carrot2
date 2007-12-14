package org.carrot2.core.constraints;

import org.carrot2.core.parameters.Constraint;

public class RangeImplementator<T extends Number & Comparable<T>> implements
    Constraint<T>
{

    T min;

    T max;
    
    public RangeImplementator()
    {
    }
    
    public RangeImplementator(T min, T max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public <V extends T> boolean isMet(V value)
    {
        return (min.compareTo(value) <= 0 && max.compareTo(value) >= 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        
        if (obj == null || !(obj instanceof RangeImplementator))
        {
            return false;
        }
        
        RangeImplementator<T> other = (RangeImplementator<T>) obj;
            
        return other.min.equals(this.min) && other.max.equals(this.max);
    }

    @Override
    public int hashCode()
    {
        final int minHash = (min != null ? min.hashCode() : 0);
        final int maxHash = (max != null ? max.hashCode() : 0);
        
        return minHash ^ maxHash;
    }
}
