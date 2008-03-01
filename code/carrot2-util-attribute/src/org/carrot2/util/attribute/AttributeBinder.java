package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import org.carrot2.util.attribute.constraint.Constraint;
import org.carrot2.util.attribute.constraint.ConstraintViolationException;


/**
 * Provides methods for binding (setting or reading) values of attributes defined by the
 * {@link Attribute} annotation.
 */
public class AttributeBinder
{
    /**
     * Contract:
     * <ul>
     * <li>Attributes annotations are required</li>
     * <li>Either Input or Output annotation is required</li>
     * <li>Attributes don't have to have default values</li>
     * <li>Map can contain null values, these will be transferred to the fields</li>
     * <li>If the map doesn't have a mapping for some key, the corresponding field will
     * not be changed</li>
     * <li>Class coercion is also performed for all binding times</li>
     * </ul>
     * TODO: provide proper docs for AttributeBinder
     */
    public static <T> void bind(T instance, Map<String, Object> values,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException
    {
        bind(new HashSet<Object>(), instance, values, bindingDirectionAnnotation,
            filteringAnnotations);
    }

    static <T> void bind(Set<Object> boundInstances, T instance,
        Map<String, Object> values,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException
    {
        // We can only bind values on classes that are @Bindable
        if (instance.getClass().getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Class is not bindable: "
                + instance.getClass().getName());
        }

        // For keeping track of circular references
        boundInstances.add(instance);

        // Get all fields (including those from bindable super classes)
        final Collection<Field> fieldSet = BindableUtils
            .getFieldsFromBindableHierarchy(instance.getClass());

        for (final Field field : fieldSet)
        {
            final String key = BindableUtils.getKey(field);
            Object value = null;

            // We skip fields that do not have all the required annotations
            if (hasAllRequiredAnnotations(field, filteringAnnotations))
            {
                // Choose the right direction
                if (Input.class.equals(bindingDirectionAnnotation)
                    && field.getAnnotation(Input.class) != null)
                {
                    final boolean required = field.getAnnotation(Required.class) != null;

                    // Transfer values from the map to the fields. If the input map
                    // doesn't contain an entry for this key, do nothing. Otherwise,
                    // perform binding as usual. This will allow to set null values
                    if (!values.containsKey(key))
                    {
                        if (required)
                        {
                            throw new AttributeBindingException(
                                "No value for required attribute: " + key);
                        }
                        continue;
                    }

                    // Note that the value can still be null here
                    value = values.get(key);

                    // TODO: Should required mean also not null? I'm only 99% convinced...
                    if (value == null && required)
                    {
                        // TODO: maybe we should have some dedicated exception here?
                        throw new AttributeBindingException(
                            "No value for required attribute: " + key);
                    }

                    // Try to coerce from class to its instance first
                    // Notice that if some extra annotations are provided, the newly
                    // created instance will get only those attributes bound that
                    // match any of the extra annotations.
                    if (value instanceof Class)
                    {
                        final Class<?> clazz = ((Class<?>) value);
                        try
                        {
                            value = clazz.newInstance();
                            bind(value, values, Input.class, filteringAnnotations);
                        }
                        catch (final InstantiationException e)
                        {
                            throw new InstantiationException(
                                "Could not create instance of class: " + clazz.getName()
                                    + " for attribute " + key);
                        }
                        catch (final IllegalAccessException e)
                        {
                            throw new InstantiationException(
                                "Could not create instance of class: " + clazz.getName()
                                    + " for attribute " + key);
                        }
                    }

                    if (value != null)
                    {
                        // Check constraints
                        final Constraint constraint = BindableUtils.getConstraint(field);
                        if (constraint != null)
                        {
                            if (!constraint.isMet(value))
                            {
                                throw new ConstraintViolationException(key, constraint,
                                    value);
                            }
                        }
                    }

                    // Finally, set the field value
                    try
                    {
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                    catch (final Exception e)
                    {
                        throw new AttributeBindingException("Could not assign field "
                            + instance.getClass().getName() + "#" + field.getName()
                            + " with value " + value, e);
                    }
                }
                else if (Output.class.equals(bindingDirectionAnnotation)
                    && field.getAnnotation(Output.class) != null)
                {
                    // Transfer values from fields to the map here
                    try
                    {
                        field.setAccessible(true);
                        value = field.get(instance);
                        values.put(key, value);
                    }
                    catch (final Exception e)
                    {
                        throw new AttributeBindingException("Could not get field value "
                            + instance.getClass().getName() + "#" + field.getName());
                    }
                }
            }
            else
            {
                // Just get the @Bindable value to perform a recursive call on it below
                try
                {
                    field.setAccessible(true);
                    value = field.get(instance);
                }
                catch (final Exception e)
                {
                    throw new AttributeBindingException("Could not get field value "
                        + instance.getClass().getName() + "#" + field.getName());
                }
            }

            // If value is not null and its class is @Bindable, we must descend into it
            if (value != null && value.getClass().getAnnotation(Bindable.class) != null)
            {
                // Check for circular references
                if (boundInstances.contains(value))
                {
                    throw new UnsupportedOperationException(
                        "Circular references are not supported");
                }

                // Recursively descend into other types.
                bind(value, values, bindingDirectionAnnotation, filteringAnnotations);
            }
        }
    }

    /**
     * Checks if all required annotations are provided.
     * 
     * @return <code>true</code> if the field has all required annotations
     * @throws IllegalArgumentException in case of any missing annotations to ease
     *             debugging
     */
    private static boolean hasAllRequiredAnnotations(Field field,
        Class<? extends Annotation>... filteringAnnotations)
    {
        final boolean hasAttribute = field.getAnnotation(Attribute.class) != null;
        boolean hasBindingDirection = field.getAnnotation(Input.class) != null
            || field.getAnnotation(Output.class) != null;

        boolean hasExtraAnnotations = filteringAnnotations.length == 0;
        for (Class<? extends Annotation> filteringAnnotation : filteringAnnotations)
        {
            hasExtraAnnotations |= field.getAnnotation(filteringAnnotation) != null;
        }

        if (hasAttribute)
        {
            if (!hasBindingDirection)
            {
                throw new IllegalArgumentException(
                    "Define binding direction annotation (@"
                        + Input.class.getSimpleName() + " or @"
                        + Output.class.getSimpleName() + ") for field "
                        + field.getClass().getName() + "#" + field.getName());
            }
        }
        else
        {
            if (hasBindingDirection || hasExtraAnnotations)
            {
                throw new IllegalArgumentException(
                    "Binding time or direction defined for a field (" + field.getClass()
                        + "#" + field.getName() + ") that does not have an @"
                        + Attribute.class.getSimpleName() + " annotation");
            }
        }

        return hasAttribute && hasExtraAnnotations;
    }
}
