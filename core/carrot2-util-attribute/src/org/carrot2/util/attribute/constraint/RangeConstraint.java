package org.carrot2.util.attribute.constraint;

/**
 * A common implementation of all range constraints involving {@link Comparable}s.
 */
class RangeConstraint extends Constraint
{
    Comparable<Object> min;
    Comparable<Object> max;

    RangeConstraint()
    {
    }

    RangeConstraint(Comparable<Object> min, Comparable<Object> max)
    {
        this.min = min;
        this.max = max;
    }

    @SuppressWarnings("unchecked")
    boolean isMet(Object value)
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

        final RangeConstraint other = (RangeConstraint) obj;

        return other.min.equals(this.min) && other.max.equals(this.max);
    }

    @Override
    public int hashCode()
    {
        final int minHash = (min != null ? min.hashCode() : 0);
        final int maxHash = (max != null ? max.hashCode() : 0);

        return minHash ^ maxHash;
    }

    @Override
    public String toString()
    {
        return "range(min = " + min.toString() + ", max = " + max.toString() + ")";
    }
}
