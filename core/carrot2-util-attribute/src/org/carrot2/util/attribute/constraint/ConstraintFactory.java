package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Builds constraint implementation from constraint annotations.
 */
class ConstraintFactory
{
    /**
     * Create a list of constraints based on the provided <code>annotations</code>.
     */
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

    /**
     * Creates a class implementing the provided constraint annotation.
     */
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

    /**
     * Sets fields of the constraint implementation based on the annotation parameters.
     */
    private static void assignFieldValues(Constraint implementator, Annotation ann)
        throws IllegalAccessException, NoSuchFieldException, InvocationTargetException
    {
        final Method [] methods = ann.annotationType().getDeclaredMethods();
        for (final Method method : methods)
        {
            try
            {
                final Field field = implementator.getClass().getDeclaredField(
                    method.getName());
                field.setAccessible(true);
                field.set(implementator, method.invoke(ann));
            }
            catch (NoSuchFieldException e) 
            {
                /*
                 * This mapping between methods and fields is so hacky... be verbose
                 * about the error.
                 */
                throw new RuntimeException("Constraint " + ann.annotationType().getName()
                    + " declares a field (method): '" + method.getName() +
                    "' for which there is no corresponding field in the implementation" +
                    " class: " + implementator.getClass().getName());
            }
        }
    }
}
