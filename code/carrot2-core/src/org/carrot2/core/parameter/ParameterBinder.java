package org.carrot2.core.parameter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.carrot2.core.constraint.Constraint;
import org.carrot2.core.constraint.ConstraintViolationException;

public class ParameterBinder
{
    /**
     * Initialize a given object with default values of instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     */
    public static <T> void bind(T instance, Map<String, Object> values,
        BindingPolicy policy) throws InstantiationException
    {
        if (instance.getClass().getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Class is not bindable: "
                + instance.getClass().getName());
        }

        final Collection<ParameterDescriptor> parameterDescriptors = ParameterDescriptorBuilder
            .getParameterDescriptors(instance.getClass(), policy);

        final Map<String, Field> fields = ParameterDescriptorBuilder.getFieldMap(instance
            .getClass(), policy);

        for (ParameterDescriptor parameterDescriptor : parameterDescriptors)
        {
            Object value = values.get(parameterDescriptor.getKey());

            // Try to coerce from class to its instance first
            if (value instanceof Class)
            {
                if (policy == BindingPolicy.RUNTIME)
                {
                    throw new RuntimeException(
                        "Only instantiation-time parameters can "
                            + "be bound to class values, offending field: "
                            + parameterDescriptor.getKey());
                }

                Class<?> clazz = ((Class<?>) value);
                try
                {
                    value = clazz.newInstance();
                }
                catch (IllegalAccessException e)
                {
                    throw new InstantiationException(
                        "Could not create instance of class: " + clazz.getName()
                            + " for parameter " + parameterDescriptor.getKey());
                }
            }

            if (value != null)
            {
                // Check constraints
                Constraint constraint = parameterDescriptor.getConstraint();
                if (constraint != null)
                {
                    if (!constraint.isMet(value))
                    {
                        // TODO: should we really throw an exception here?
                        throw new ConstraintViolationException(parameterDescriptor,
                            constraint, value);
                    }
                }
            }

            if (value == null)
            {
                // check the default value of parameter.
                value = parameterDescriptor.getDefaultValue();
            }

            final Field field = fields.get(parameterDescriptor.key);
            final Parameter binding = field.getAnnotation(Parameter.class);

            // TODO: if there is no default value provided for a parameter and
            // no value for the parameter in the map, we'll get a NPE here.
            // Should we do something about it? For the time being, don't descend if there
            // is no value.
            if (binding != null && value != null
                && value.getClass().getAnnotation(Bindable.class) != null)
            {
                // Recursively descend into other types.
                bind(value, values, policy);
            }

            try
            {
                field.set(instance, value);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Could not assign field "
                    + instance.getClass().getName() + "#" + field.getName()
                    + " with value " + value);
            }
        }
    }

    /**
     * Create an instance of a given class and initialize it with default values of
     * instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     */
    public static <T> T createInstance(Class<T> clazz, Map<String, Object> values)
        throws InstantiationException
    {
        final T instance;
        try
        {
            instance = clazz.newInstance();
        }
        catch (IllegalAccessException e)
        {
            throw new InstantiationException(
                "Could not create instance (illegal access): " + e);
        }

        bind(instance, values, BindingPolicy.INSTANTIATION);

        return instance;
    }
}
