
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
    protected void checkAssignableFrom(Object value, Class<?>... clazz)
    {
        /*
         * Null is assignable to anything.
         */
        if (value == null)
        {
            return;
        }

        if (clazz.length == 0)
        {
            throw new IllegalArgumentException("Classes array must not be empty");
        }
        
        for (Class<?> c : clazz)
        {
            if (c.isAssignableFrom(value.getClass()))
            {
                return;
            }
        }
        throw new IllegalArgumentException("Expected an instance of "
            + classesToString(clazz) + " but found " + value.getClass().getName());
    }

    private static String classesToString(Class<?>...classes)
    {
        if (classes.length == 1)
        {
            return classes[0].getName();
        }
        else
        {
            StringBuilder s = new StringBuilder("any of");
            s.append(classes[0].getName());
            for (int i = 1; i < classes.length; i++)
            {
                s.append(", ");
                s.append(classes[i].getName());
            }
            return s.toString();
        }
    }

    /**
     * TODO: remove this method and replace with a constructor accepting Annotation this
     * constraint is bound to.
     */
    public final void populate(Annotation annotation)
    {
        this.annotation = annotation;
        populateCustom(annotation);
    }

    protected void populateCustom(Annotation annotation)
    {
    }
}
