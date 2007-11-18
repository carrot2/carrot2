/**
 * 
 */
package org.carrot2.core.parameters;

import java.util.*;

import org.carrot2.util.ObjectUtils;

/**
 *
 */
public class ValueSetConstraint<T> implements Constraint<T>
{
    /** */
    private final Set<? extends T> values;

    /**
     * 
     */
    public <V extends T> ValueSetConstraint(V... values)
    {
        this.values = new HashSet<T>(Arrays.asList(values));
    }

    @Override
    public <V extends T> boolean isMet(V value)
    {
        return values.contains(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof ValueSetConstraint))
        {
            return false;
        }

        ValueSetConstraint<T> other = (ValueSetConstraint<T>) obj;

        return ObjectUtils.equals(other.values, this.values);
    }

    @Override
    public int hashCode()
    {
        return values.hashCode();
    }
}
