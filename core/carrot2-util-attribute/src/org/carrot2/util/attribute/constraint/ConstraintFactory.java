
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
import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.ExceptionUtils;

import com.google.common.collect.Lists;

/**
 * Builds constraint implementation from constraint annotations.
 */
public class ConstraintFactory
{
    /**
     * Create a list of constraints based on the provided <code>annotations</code>.
     */
    public static List<Constraint> createConstraints(Annotation... annotations)
    {
        final ArrayList<Constraint> constraints = Lists.newArrayList();
        for (Annotation annotation : annotations)
        {
            if (isConstraintAnnotation(annotation.annotationType()))
            {
                constraints.add(createImplementation(annotation));
            }
        }

        return constraints;
    }

    /**
     * Returns <code>true</code> if the provided annotation is a constraint annotation.
     */
    static boolean isConstraintAnnotation(Class<? extends Annotation> ann)
    {
        return ann.isAnnotationPresent(IsConstraint.class);
    }

    /**
     * Creates a class implementing the provided constraint annotation.
     */
    static Constraint createImplementation(Annotation ann)
    {
        final IsConstraint constraintAnnotation = ann.annotationType().getAnnotation(
            IsConstraint.class);
        final Class<?> implClass = constraintAnnotation.implementation();
        if (!Constraint.class.isAssignableFrom(implClass))
        {
            throw new IllegalArgumentException("Implementation class "
                + implClass.getClass().getName() + " must implement "
                + Constraint.class.getName());
        }

        Constraint instance;
        try
        {
            instance = (Constraint) implClass.newInstance();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
        instance.populate(ann);

        return instance;
    }
}
