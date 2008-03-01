package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

class ConstraintFactory
{
    static List<Constraint> createConstraints(Annotation... annotations)
    {
        final ArrayList<Constraint> constraints = Lists.newArrayList();
        for (Annotation annotation : annotations)
        {
            if (isConstraintAnnotation(annotation.annotationType()))
            {
                constraints.add(createConstraint(annotation));
            }
        }

        return constraints;
    }

    static boolean isConstraintAnnotation(Class<? extends Annotation> ann)
    {
        return ann.isAnnotationPresent(IsConstraint.class);
    }

    static Constraint createImplementation(IsConstraint ann)
        throws InstantiationException, IllegalAccessException
    {
        final Class<?> implClass = ann.implementation();
        if (!Constraint.class.isAssignableFrom(implClass))
        {
            throw new IllegalArgumentException("Implementation class "
                + implClass.getClass().getName() + " must implement "
                + Constraint.class.getName());
        }
        return (Constraint) implClass.newInstance();
    }

    private static Constraint createConstraint(Annotation annotation)
    {
        try
        {
            final Constraint implementation = createImplementation(annotation
                .annotationType().getAnnotation(IsConstraint.class));
            implementation.annotation = annotation;
            assignFieldValues(implementation, annotation);
            return implementation;
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Could not create constraint instance", e);
        }
    }

    private static void assignFieldValues(Constraint implementator, Annotation ann)
        throws IllegalAccessException, NoSuchFieldException, InvocationTargetException
    {
        final Method [] methods = ann.annotationType().getDeclaredMethods();
        for (final Method method : methods)
        {
            final Field field = implementator.getClass().getDeclaredField(
                method.getName());
            field.setAccessible(true);
            field.set(implementator, method.invoke(ann));
        }
    }
}
