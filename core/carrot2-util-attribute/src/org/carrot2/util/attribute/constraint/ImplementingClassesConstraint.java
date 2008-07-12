package org.carrot2.util.attribute.constraint;

/**
 * Implementation of the {@link ImplementingClasses} constraint.
 */
class ImplementingClassesConstraint extends Constraint
{
    /**
     * Auto-assigned by {@link ConstraintFactory}.
     * 
     * @see ImplementingClasses#classes()
     */
    private Class<?> [] classes;
    
    /**
     * Auto-assigned by {@link ConstraintFactory}.
     * 
     * @see ImplementingClasses#strict()
     */
    private boolean strict;

    ImplementingClassesConstraint()
    {
    }

    ImplementingClassesConstraint(Class<?> [] classes)
    {
        this.classes = classes;
    }

    boolean isMet(Object value)
    {
        /*
         * I assume <code>null</code> values are not satisfying this
         * constraint (even though <code>null</code>s can be assigned to 
         * any class).
         */
        if (value == null)
        {
            return true;
        }

        final Class<?> target = value.getClass();
        for (final Class<?> clazz : classes)
        {
            if (clazz.isAssignableFrom(target))
            {
                return true;
            }
        }

        return !strict;
    }
}
