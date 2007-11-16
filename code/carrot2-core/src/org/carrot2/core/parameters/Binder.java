package org.carrot2.core.parameters;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.carrot2.core.Configurable;
import org.carrot2.core.type.AnyClassTypeWithDefaultValue;
import org.carrot2.core.type.TypeWithDefaultValue;

public class Binder
{
    public static <T extends Configurable> T createInstance(Class<T> clazz, Map<String, Object> values)
        throws InstantiationException
    {
        final Collection<Parameter> instanceParameters = 
            ParameterBuilder.getParameters(clazz, BindingPolicy.INSTANTIATION);

        final T instance;
        try
        {
            instance = clazz.newInstance();
        }
        catch (IllegalAccessException e)
        {
            throw new InstantiationException("Could not create instance (illegal access): " + e);
        }

        final Map<String, Field> fields = ParameterBuilder.getFieldMap(clazz, BindingPolicy.INSTANTIATION);

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
                value = createInstance((Class) value, values);
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
}
