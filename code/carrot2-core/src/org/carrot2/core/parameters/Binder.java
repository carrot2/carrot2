package org.carrot2.core.parameters;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.carrot2.core.Configurable;
import org.carrot2.core.type.AnyClassTypeWithDefaultValue;
import org.carrot2.core.type.TypeWithDefaultValue;

public class Binder
{
    /**
     * Initialize a given object with default values of instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     */
    public static <T> T initializeInstance(T instance, Map<String, Object> values)
        throws InstantiationException
    {
        final Collection<Parameter> instanceParameters = ParameterBuilder.getParameters(
            instance.getClass(), BindingPolicy.INSTANTIATION);

        final Map<String, Field> fields = ParameterBuilder.getFieldMap(instance
            .getClass(), BindingPolicy.INSTANTIATION);

        for (Parameter p : instanceParameters)
        {
            Object value = values.get(p.getName());
            if (value == null)
            {
                // check the default value of parameter.
                TypeWithDefaultValue<?> t = (TypeWithDefaultValue<?>) p.type;
                value = t.getDefaultValue();
            }

            if (p.type instanceof AnyClassTypeWithDefaultValue)
            {
                // Recursively descend into other types.
                value = initializeInstance(value, values);
            }

            try
            {
                fields.get(p.name).set(instance, value);
            }
            catch (Exception e)
            {
                throw new InstantiationException("Could not assign field "
                    + instance.getClass().getName() + "#" + p.name + " with value "
                    + value);
            }
        }

        return instance;
    }

    /**
     * Create an instance of a given class and initialize it with default values of
     * instance-time binding parameters ({@link BindingPolicy#INSTANTIATION}).
     */
    public static <T extends Configurable> T createInstance(Class<T> clazz,
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

        return initializeInstance(instance, values);
    }
}
