
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

/**
 * A base class for implementing constraints.
 */
public abstract class Constraint
{
    /** Annotation corresponding to this constraint */
    protected Annotation annotation;

    /**
     * Checks if the provided <code>value</code> meets this constraint.
     */
    protected abstract boolean isMet(Object value);

    /**
     * Checks if the provided <code>value</code> can be assigned to the type defined by
     * <code>clazz</code>. If not, an {@link IllegalArgumentException} will be thrown.
     * <code>null</code> values are assignable to any class.
     */
    protected void checkAssignableFrom(Class<?> clazz, Object value)
    {
        /*
         * Null is assignable to anything.
         */
        if (value == null)
        {
            return;
        }

        if (!clazz.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Expected an instance of "
                + clazz.getName() + " but found " + value.getClass().getName());
        }
    }

    public final void populate(Annotation annotation)
    {
        this.annotation = annotation;
        populateCustom(annotation);
    }

    protected void populateCustom(Annotation annotation)
    {
    }
}
