package org.carrot2.core.parameters;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.carrot2.core.type.Type;

/**
 * 
 */
public class ParameterBuilder {
	/**
	 * 
	 */
	public static Collection<Parameter> getParameters(Class<?> clazz, BindingPolicy policy)
	{
	    // Output array of parameters.
		final ArrayList<Parameter> params = new ArrayList<Parameter>();

		// Get the field names that correspond to the requested policy.
		final Collection<Field> bindableFields = getFieldMap(clazz, policy).values();
		
        // Build a map of fields, normalize field names. 
        final HashSet<Field> remainingFields = new HashSet<Field>(
            Arrays.asList(clazz.getDeclaredFields()));
        remainingFields.removeAll(bindableFields);
        setAccessible(remainingFields.toArray(new Field[remainingFields.size()]));

        final HashMap<String,Field> remainingFieldsByName = new HashMap<String,Field>();
        for (Field f : remainingFields)
        {
            remainingFieldsByName.put(normalize(f.getName()), f);
        }

        for (final Field field : bindableFields)
		{
			final String fieldName = field.getName();
			final String normalizedFieldName = normalize(fieldName);

			final Field typeField = remainingFieldsByName.get(normalizedFieldName);
			if (typeField == null)
			{
			    throw new RuntimeException("Missing type field for bindable field: "
			        + clazz.getName() + "#" + fieldName);
			}

			if (!Modifier.isStatic(typeField.getModifiers())
					|| !Type.class.isAssignableFrom(typeField.getType()))
			{
				throw new RuntimeException("Type field must be static and inherited from "
						+ Type.class.getName() + ": " 
						+ clazz.getName() + "#" + typeField.getName());
			}

			final Type<?> type;
			try
			{
				type = (Type<?>) typeField.get(null);
			}
			catch (Exception e)
			{
                throw new RuntimeException("Could not retrieve value of type field: "
                    + clazz.getName() + "#" + typeField.getName(), e);
			}

			// TODO: check what's wrong with isAssignableFrom and primitive types.
			final Class<?> fieldClass = wrapPrimitive(field.getType());
			final Class<?> declClass = wrapPrimitive(type.getType());
			if (!fieldClass.isAssignableFrom(declClass)) {
				throw new RuntimeException("The bindable type's "
				    + clazz.getName() + "#" + typeField.getName()
				    + " value is unassignable to"
				    + clazz.getName() + "#" + fieldName);
			}

			params.add(new Parameter(fieldName, type));
		}

		return params;
	}

	/**
	 * One-way normalization of field name to conflate bindable fields and their
	 * types to a common form.
	 */
	private static String normalize(String fieldName)
    {
        return fieldName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    /*
	 * 
	 */
    public static Map<String, Field> getFieldMap(Class<?> clazz, BindingPolicy policy)
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


