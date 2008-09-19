package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

import org.simpleframework.xml.*;

/**
 * Implementation of the {@link ImplementingClasses} constraint.
 */
@Root(name = "implementing-classes")
class ImplementingClassesConstraint extends Constraint
{
    /**
     * Auto-assigned by {@link ConstraintFactory}.
     * 
     * @see ImplementingClasses#classes()
     */
    @ElementArray
    private Class<?> [] classes;

    /**
     * Auto-assigned by {@link ConstraintFactory}.
     * 
     * @see ImplementingClasses#strict()
     */
    @Attribute
    private boolean strict;

    protected boolean isMet(Object value)
    {
        /*
         * I assume <code>null</code> values are not satisfying this constraint (even
         * though <code>null</code>s can be assigned to any class).
         */
        if (value == null)
        {
            return false;
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

    @Override
    protected void populateCustom(Annotation annotation)
    {
        final ImplementingClasses implementingClasses = (ImplementingClasses) annotation;
        classes = implementingClasses.classes();
        strict = implementingClasses.strict();
    }
}
