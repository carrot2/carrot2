/**
 * 
 */
package org.carrot2.core.parameters;

import java.util.*;

import org.carrot2.util.ObjectUtils;

/**
 *
 */
public class MultipleConstraint<T> implements Constraint<T>
{
    private final Set<Constraint<T>> constraints;

    /**
     * 
     */
    public MultipleConstraint(Constraint<T>... constraints)
    {
        this.constraints = new HashSet<Constraint<T>>(Arrays.asList(constraints));
    }

    @Override
    public <V extends T> boolean isMet(V value)
    {
        for (Constraint<T> constraint : constraints)
        {
            if (!constraint.isMet(value))
            {
                return false;
            }
        }

        return true;
    }

    public MultipleConstraint<T> add(Constraint<T>... constraints)
    {
        for (int i = 0; i < constraints.length; i++)
        {
            this.constraints.add(constraints[i]);
        }
        return this;
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
        
        if (!(obj instanceof MultipleConstraint))
        {
            return false;
        }
        
        MultipleConstraint<T> other = (MultipleConstraint<T>) obj;
        
        return ObjectUtils.equals(this.constraints, other.constraints);
    }
}
