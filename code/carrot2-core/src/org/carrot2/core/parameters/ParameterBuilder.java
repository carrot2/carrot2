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

import org.carrot2.util.ClassUtils;

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

        AccessibleObject.setAccessible(
            remainingFields.toArray(new Field[remainingFields.size()]), true);

        final HashMap<String,Field> remainingFieldsByName = new HashMap<String,Field>();
        for (Field f : remainingFields)
        {
            remainingFieldsByName.put(normalize(f.getName()), f);
        }

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
        	//here: instead of looking for a static field with appropriate name
        	//annotation HasConstraint can be checked
			final String fieldName = field.getName();
			final String normalizedFieldName = normalize(fieldName + "constraint");

			final Field constraintField = remainingFieldsByName.get(normalizedFieldName);
			Constraint<?> constraint = null;
			if (constraintField != null)
			{
			    // Constraints are optional, but if they are provided, we should probably
			    // check type and modifiers
	            if (!Modifier.isStatic(constraintField.getModifiers())
	                    || !Constraint.class.isAssignableFrom(constraintField.getType()))
	            {
	                throw new RuntimeException("Constraint field must be static and inherited from "
	                        + Constraint.class.getName() + ": " 
	                        + clazz.getName() + "#" + constraintField.getName());
	            }
	            
	            try
                {
                    constraint = (Constraint<?>) constraintField.get(null);
                }
	            catch (Exception e)
	            {
	                throw new RuntimeException("Could not retrieve value of constraint field: "
	                    + clazz.getName() + "#" + constraintField.getName(), e);
	            }
			}

			// TODO: check what's wrong with isAssignableFrom and primitive types.
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

			params.add(
			    new Parameter(
			        fieldName, ClassUtils.boxPrimitive(field.getType()), fieldValue, constraint));
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
        
        // TODO: Should we remember or somehow reset the accessibility for non-descriptor fields
        // upon returning from this method?
        AccessibleObject.setAccessible(declaredFields, true);
        for (final Field field : declaredFields)
        {
            final Binding binding = field.getAnnotation(Binding.class);
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


