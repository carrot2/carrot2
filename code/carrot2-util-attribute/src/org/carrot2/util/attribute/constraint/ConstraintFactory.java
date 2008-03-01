package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

public class ConstraintFactory
{
    public static boolean isConstraintAnnotation(Class<? extends Annotation> ann)
    {
        return ann.isAnnotationPresent(IsConstraint.class);
    }

    public static Constraint createConstraint(Annotation ann)
    {
        if (!isConstraintAnnotation(ann.annotationType()))
        {
            throw new IllegalArgumentException("Provided annotation is not a constraint");
        }

        try
        {
            final Constraint implementation = createImplementation(ann.annotationType()
                .getAnnotation(IsConstraint.class));
            assignFieldValues(implementation, ann);
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
}
