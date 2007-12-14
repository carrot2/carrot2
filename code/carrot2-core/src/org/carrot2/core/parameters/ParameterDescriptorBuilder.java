package org.carrot2.core.parameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.carrot2.core.constraints.Constraint;
import org.carrot2.core.constraints.ConstraintFactory;
import org.carrot2.core.constraints.CompoundConstraint;
import org.carrot2.util.ClassUtils;

/**
 * 
 */
public class ParameterDescriptorBuilder
{
    /**
     * 
     */
    public static Collection<ParameterDescriptor> getParameters(Class<?> clazz, BindingPolicy policy)
    {
        // Output array of parameters.
        final ArrayList<ParameterDescriptor> params = new ArrayList<ParameterDescriptor>();

        // Get the field names that correspond to the requested policy.
        final Collection<Field> bindableFields = getFieldMap(clazz, policy).values();

        // This is a little hacky -- to get the default values we must create an instance
        // of the class. Assuming that we have no-parameter constructors
        // and an explicit lifecycle method doing the (usually costly) initialization,
        // we can safely create this instance here.
        Object instance;
        try
        {
            instance = clazz.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create instance of class: "
                + clazz.getName());
        }

        for (final Field field : bindableFields)
        {
            final String fieldName = field.getName();

            List<Constraint<?>> constraints = new ArrayList<Constraint<?>>();
            final Annotation [] a1 = field.getAnnotations();
            final Annotation [] a2 = field.getDeclaredAnnotations();
            
            for (Annotation annotation : field.getAnnotations())
            {
                if (ConstraintFactory.isConstraintAnnotation(annotation.annotationType()))
                {
                    constraints.add(ConstraintFactory.createConstraint(annotation));
                }
            }
            Constraint<?> constraint = null;
            if (constraints.size() == 1)
            {
                constraint = constraints.get(0);
            }
            else if (constraints.size() > 1)
            {
                constraint = new CompoundConstraint(constraints);
            }

            Object fieldValue;
            try
            {
                fieldValue = field.get(instance);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not retrieve default value of field: "
                    + fieldName);
            }

            params.add(new ParameterDescriptor(fieldName, ClassUtils.boxPrimitive(field.getType()),
                fieldValue, constraint));
        }

        return params;
    }

    /*
     * 
     */
    public static Map<String, Field> getFieldMap(Class<?> clazz, BindingPolicy policy)
    {
        final Field [] declaredFields = clazz.getDeclaredFields();
        final Map<String, Field> result = new HashMap<String, Field>(
            declaredFields.length);

        // TODO: Should we remember or somehow reset the accessibility for non-descriptor
        // fields
        // upon returning from this method?
        AccessibleObject.setAccessible(declaredFields, true);
        for (final Field field : declaredFields)
        {
            final Parameter binding = field.getAnnotation(Parameter.class);
            if (binding == null)
            {
                continue;
            }

            if (binding.policy() == policy)
            {
                result.put(field.getName(), field);
            }
        }

        return result;
    }
}
