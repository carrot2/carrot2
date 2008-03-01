/**
 *
 */
package org.carrot2.util.attribute.constraint;

import java.util.*;

import org.apache.commons.lang.ObjectUtils;

/**
 *
 */
public class CompoundConstraint implements Constraint
{
    private final Set<Constraint> constraints;

    public CompoundConstraint(Collection<? extends Constraint> constraints)
    {
        this.constraints = new HashSet<Constraint>(constraints);
    }

    public CompoundConstraint(Constraint... constraints)
    {
        this.constraints = new HashSet<Constraint>(Arrays.asList(constraints));
    }

    public boolean isMet(Object value)
    {
        for (final Constraint constraint : constraints)
        {
            if (!constraint.isMet(value))
            {
                return false;
            }
        }

        return true;
    }

    Set<Constraint> getConstraints()
    {
        return constraints;
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

        final CompoundConstraint other = (CompoundConstraint) obj;

        return ObjectUtils.equals(this.constraints, other.constraints);
    }
}
