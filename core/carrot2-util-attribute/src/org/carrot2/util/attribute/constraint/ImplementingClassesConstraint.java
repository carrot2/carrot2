
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
         * <code>null</code> values satisfy this constraint. Use @Required
         * to avoid null values.
         */
        if (value == null)
        {
            return true;
        }

        /*
         * http://issues.carrot2.org/browse/CARROT-536
         * 
         * The value of an attribute annotated with @ImplementingClasses
         * may be a class instance (in which case the binder will create an instance when
         * initializing the bindable).
         */
        final Class<?> target;
        if (value instanceof Class<?>)
        {
            target = (Class<?>) value;

            // TODO: Check for other special cases by simply trying to instantiate target? 
            if (target.isInterface())
            {
                return false;
            }
        }
        else
        {
            target = value.getClass();            
        }

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
