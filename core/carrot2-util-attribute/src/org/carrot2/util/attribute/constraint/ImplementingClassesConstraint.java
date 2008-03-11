package org.carrot2.util.attribute.constraint;

/**
 * Implementation of the {@link ImplementingClasses} constraint.
 */
class ImplementingClassesConstraint extends Constraint
{
    private Class<?> [] classes;

    ImplementingClassesConstraint()
    {
    }

    ImplementingClassesConstraint(Class<?> [] classes)
    {
        this.classes = classes;
    }

    boolean isMet(Object value)
    {
        for (final Class<?> clazz : classes)
        {
            if (clazz.equals(value.getClass()))
            {
                return true;
            }
        }

        return false;
    }
}
