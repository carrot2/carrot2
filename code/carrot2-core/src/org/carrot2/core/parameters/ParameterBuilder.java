package org.carrot2.core.parameters;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.Configurable;
import org.carrot2.core.type.Type;

/**
 * 
 */
public class ParameterBuilder {
	/**
	 * 
	 */
	public static Collection<Parameter> getParameters(Class<? extends Configurable> clazz, BindingPolicy policy)
	{
		final ArrayList<Parameter> params = new ArrayList<Parameter>();

		final Field [] allFields = clazz.getDeclaredFields();
		setAccessible(allFields);

		final Collection<Field> fields = getFieldMap(clazz, policy).values();
outer:
        for (final Field field : fields)
		{
			final String fieldName = field.getName();
			// TODO: implement support for camel-case--underscore convention.
			final String typeFieldName = fieldName.toUpperCase();

			for (Field f : allFields)
			{
				if (typeFieldName.equals(f.getName()))
				{
					if (!Modifier.isStatic(f.getModifiers())
							|| !Type.class.isAssignableFrom(f.getType()))
					{
						throw new RuntimeException("Field must be static and of type "
								+ Type.class.getName() + ": " + f);
					}

					try
					{
						final Type<?> type = (Type<?>) f.get(null);
						// TODO: check what's wrong with isassignablefrom and primitive types.
						final Class<?> fieldClass = wrapPrimitive(field.getType());
						final Class<?> declClass = wrapPrimitive(type.getType());
						if (!fieldClass.isAssignableFrom(declClass)) {
							throw new RuntimeException("Types differ.");
						}
						params.add(new Parameter(fieldName, type));
					}
					catch (Exception e)
					{
						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						}
						throw new RuntimeException(e);
					}
					continue outer;
				}
			}
            throw new RuntimeException("Missing descriptor field for: " + fieldName);
		}

		return params;
	}

	/*
	 * 
	 */
    public static Map<String, Field> getFieldMap(Class<? extends Configurable> clazz, BindingPolicy policy)
    {
        final Field [] declaredFields = clazz.getDeclaredFields();
        final Map<String, Field> result = new HashMap<String, Field>(declaredFields.length);
        
        setAccessible(declaredFields);
        
        for (final Field field : declaredFields)
        {
            final Binding binding = field.getAnnotation(Binding.class);
            if (binding == null)
            {
                continue;
            }
            
            if (binding.value() == policy)
            {
                result.put(field.getName(), field);
            }
        }

        return result;
    }
	
    /*
     * 
     */
	private static void setAccessible(AccessibleObject... accessible)
    {
        // TODO: check accessibility jumpout -- howto and why.
        for (final AccessibleObject field : accessible)
        {
            field.setAccessible(true);
        }
    }

    private final static HashMap<Class<?>,Class<?>> types = 
		new HashMap<Class<?>,Class<?>>();

	static {
		types.put(byte.class, Byte.class);
		types.put(short.class, Short.class);
		types.put(int.class, Integer.class);
		types.put(long.class, Long.class);
		types.put(float.class, Float.class);
		types.put(double.class, Double.class);
		types.put(char.class, Character.class);
	}

	/*
	 * 
	 */
	private static Class<?> wrapPrimitive(Class<?> type) {
		if (type.isPrimitive()) {
			return types.get(type);
		}
		return type;
	}
}


