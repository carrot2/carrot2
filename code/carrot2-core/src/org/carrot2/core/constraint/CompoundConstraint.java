/**
 * 
 */
package org.carrot2.core.constraint;

import java.util.*;

import org.carrot2.util.ObjectUtils;

/**
 *
 */
public class CompoundConstraint implements Constraint
{
    private final Set<Constraint> constraints;

    /**
     * 
     */
    public CompoundConstraint(Constraint... constraints)
    {
        this.constraints = new HashSet<Constraint>(Arrays.asList(constraints));
    }

    public CompoundConstraint(Collection<? extends Constraint> constraints)
    {
        this.constraints = new HashSet<Constraint>(constraints);
    }
    
    @Override
    public boolean isMet(Object value)
    {
        for (Constraint constraint : constraints)
        {
            if (!constraint.isMet(value))
            {
                return false;
            }
        }

        return true;
    }

    public CompoundConstraint add(Constraint... constraints)
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
        
        CompoundConstraint other = (CompoundConstraint) obj;
        
        return ObjectUtils.equals(this.constraints, other.constraints);
    }
}
