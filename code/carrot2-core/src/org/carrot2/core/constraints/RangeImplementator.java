package org.carrot2.core.constraints;

import org.carrot2.core.parameters.Constraint;

public class RangeImplementator<T extends Number & Comparable<T>> implements
    Constraint<T>
{

    T min;

    T max;

    @Override
    public <V extends T> boolean isMet(V value)
    {
        return (min.compareTo(value) <= 0 && max.compareTo(value) >= 0);
    }
}
