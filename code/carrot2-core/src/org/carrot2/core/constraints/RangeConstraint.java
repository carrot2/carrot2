package org.carrot2.core.constraints;

public class RangeConstraint implements Constraint
{
    @SuppressWarnings("unchecked")
    private Comparable min;
    
    @SuppressWarnings("unchecked")
    private Comparable max;

    public RangeConstraint()
    {
    }
    
    public RangeConstraint(Comparable<?> min, Comparable<?> max)
    {
        this.min = min;
        this.max = max;
    }

    @SuppressWarnings("unchecked")
    public boolean isMet(Object value)
    {
        return (value instanceof Comparable<?>)
            && (min.compareTo(value) <= 0 && max.compareTo(value) >= 0);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof RangeConstraint))
        {
            return false;
        }

        RangeConstraint other = (RangeConstraint) obj;

        return other.min.equals(this.min) && other.max.equals(this.max);
    }

    @Override
    public int hashCode()
    {
        final int minHash = (min != null ? min.hashCode() : 0);
        final int maxHash = (max != null ? max.hashCode() : 0);

        return minHash ^ maxHash;
    }
    
    public Comparable<?> getMin()
    {
        return min;
    }

    public Comparable<?> getMax()
    {
        return max;
    }
    
    @Override
    public String toString()
    {
        return "range(min = " + min.toString() + ", max = " + max.toString() + ")";
    }
}
