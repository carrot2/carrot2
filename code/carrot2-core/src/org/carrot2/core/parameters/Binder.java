package org.carrot2.core.parameters;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class Binder
{
    /**
     * Initialize a given object with default values of instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     * TODO: if parameters contain constraints, check them here
     */
    public static <T> void bind(T instance, Map<String, Object> values, BindingPolicy policy)
        throws InstantiationException
    {
        if (instance.getClass().getAnnotation(Bindable.class) == null) {
            throw new IllegalArgumentException("Class is not bindable: " + instance.getClass().getName());
        }

        final Collection<ParameterDescriptor> parameters = ParameterDescriptionBuilder.getParameters(
            instance.getClass(), policy);

        final Map<String, Field> fields = ParameterDescriptionBuilder.getFieldMap(
            instance.getClass(), policy);

        for (ParameterDescriptor p : parameters)
        {
            Object value = values.get(p.getName());
            
            // Try to coerce from class to its instance first
            if (value instanceof Class)
            {
                if (policy == BindingPolicy.RUNTIME)
                {
                    throw new InstantiationException("Only instantiation-time parameters can "
                        + " be bound to class values, offending field: " + p.getName());
                }

                Class<?> clazz = ((Class<?>)value);
                try
                {
                    value = clazz.newInstance();
                }
                catch (IllegalAccessException e)
                {
                    throw new InstantiationException(
                        "Could not create instance of class:" + clazz.getName()
                            + " for parameter " + p.getName());
                }
            }
            
            if (value == null)
            {
                // check the default value of parameter.
                value = p.getDefaultValue();
            }

            final Field field = fields.get(p.name);
            final Parameter binding = field.getAnnotation(Parameter.class);

            if (binding != null && value.getClass().getAnnotation(Bindable.class) != null)
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
                throw new InstantiationException("Could not assign field "
                    + instance.getClass().getName() + "#" + p.name + " with value "
                    + value);
            }
        }
    }

    /**
     * Create an instance of a given class and initialize it with default values of
     * instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     */
    public static <T> T createInstance(Class<T> clazz,
        Map<String, Object> values) throws InstantiationException
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
