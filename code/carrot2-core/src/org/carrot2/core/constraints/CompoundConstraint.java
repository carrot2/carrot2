/**
 * 
 */
package org.carrot2.core.constraints;

import java.util.*;

import org.carrot2.util.ObjectUtils;

/**
 *
 */
public class CompoundConstraint<T> implements Constraint<T>
{
    private final Set<Constraint<T>> constraints;

    /**
     * 
     */
    public CompoundConstraint(Constraint<T>... constraints)
    {
        this.constraints = new HashSet<Constraint<T>>(Arrays.asList(constraints));
    }

    public CompoundConstraint(Collection<? extends Constraint<T>> constraints)
    {
        this.constraints = new HashSet<Constraint<T>>(constraints);
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

    public CompoundConstraint<T> add(Constraint<T>... constraints)
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
        
        if (!(obj instanceof CompoundConstraint))
        {
            return false;
        }
        
        CompoundConstraint<T> other = (CompoundConstraint<T>) obj;
        
        return ObjectUtils.equals(this.constraints, other.constraints);
    }
}
