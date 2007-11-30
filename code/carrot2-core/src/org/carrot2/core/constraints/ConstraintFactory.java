package org.carrot2.core.constraints;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.carrot2.core.parameters.Constraint;

public class ConstraintFactory
{

    public static Constraint<?> createConstraint(Annotation ann)
    {
        if (!isConstraintAnnotation(ann.annotationType()))
        {
            throw new ConstraintCreationException();
        }

        try
        {
            Constraint<?> implementator = createImplementatorInstance(ann
                .annotationType().getAnnotation(IsConstraint.class));
            assignFieldValues(implementator, ann);
            return implementator;
        }
        catch (Exception e)
        {
            throw new ConstraintCreationException(e);
        }
    }

    static void assignFieldValues(Constraint<?> implementator, Annotation ann)
        throws IllegalAccessException, NoSuchFieldException, InvocationTargetException
    {
        // Field [] fields = ann.annotationType().getFields();
        Method [] methods = ann.annotationType().getDeclaredMethods();
        for (Method method : methods)
        {
            implementator.getClass().getDeclaredField(method.getName()).set(
                implementator, method.invoke(ann));
        }
    }

    static Constraint<?> createImplementatorInstance(IsConstraint ann)
        throws InstantiationException, IllegalAccessException
    {
        Class<?> implClass = ann.implementator();
        if (!Constraint.class.isAssignableFrom(implClass))
        {
            throw new ConstraintCreationException();
        }
        return (Constraint<?>) implClass.newInstance();
        // return RangeImplementator.createForClass(Integer.class);
    }

    static boolean isConstraintAnnotation(Class<? extends Annotation> ann)
    {
        return ann.isAnnotationPresent(IsConstraint.class);
    }
}
