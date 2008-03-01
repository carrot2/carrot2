package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import org.carrot2.util.attribute.constraint.ConstraintValidator;
import org.carrot2.util.attribute.constraint.ConstraintViolationException;

/**
 * Provides methods for binding (setting and collecting) values of attributes defined by
 * the {@link Attribute} annotation.
 */
public class AttributeBinder
{
    /**
     * Performs binding (setting or collecting) of {@link Attribute} values on the
     * provided <code>instance</code>. The direction of binding, i.e. whether
     * attributes will be set or collected from the <code>object</code> depends on the
     * provided <code>bindingDirectionAnnotation</code>, which can be either
     * {@link Input} or {@link Output} for setting and collecting attribute values of the
     * <code>object</code>, respectively.
     * <p>
     * Binding will be performed for all attributes of the provided <code>object</code>,
     * no matter where in the <code>object</code>'s hierarchy the attribute is
     * declared. Binding will recursively descend into all fields of the
     * <code>object</code> whose types are marked with {@link Bindable}, no matter
     * whether these fields are attributes or not.
     * <p>
     * Keys of the <code>values</code> map are interpreted as attribute keys as defined
     * by {@link Attribute#key()}. When setting attribute values, the map must contain
     * non-<code>null</code> mappings for all {@link Required} attributes, otherwise an
     * {@link AttributeBindingException} will be thrown. If the map has no mapping for
     * some non-{@link Required} attribute, the value of that attribute will not be
     * changed. However, if the map contains a <code>null</code> mapping for some non-{@link Required}
     * attribute, the value that attribute will be set to <code>null</code>.
     * <p>
     * When setting attributes, values will be transferred from the map without any
     * conversion with one exception. If the type of the attribute field is not
     * {@link Class} and the corresponding value in the <code>values</code> map is of
     * type {@link Class}, an attempt will be made to coerce the class to a corresponding
     * instance by calling its parameterless constructor. If the created type is
     * {@link Bindable}, an attempt will also be made to bind attributes of the newly
     * created object using the <code>values</code> map, current
     * <code>bindingDirectionAnnotation</code> and <code>filteringAnnotations</code>.
     * <p>
     * Before value of an attribute is set, the new value is checked against all
     * constraints defined for the attribute and must meet all these constraints.
     * Otherwise, the {@link ConstraintViolationException} will be thrown.
     * <p>
     * 
     * @param object the object to set or collect attributes from. The type of the
     *            provided object must be annotated with {@link Bindable}.
     * @param values the values of {@link Input} attributes to be set or a placeholder for
     *            {@link Output} attributes to be collected. If attribute values are to be
     *            collected, the provided Map must be modifiable.
     * @param bindingDirectionAnnotation {@link Input} if attribute values are to be set
     *            on the provided <code>object</code>, or {@link Output} if attribute
     *            values are to be collected from the <code>object</code>.
     * @param filteringAnnotations additional domain-specific annotations that the
     *            attribute fields must have in order to be bound. This parameter can be
     *            used to selectively bind different set of attributes depending, e.g. on
     *            the life cycle of the <code>object</code>.
     * @throws InstantiationException if coercion of a class attribute value to an
     *             instance fails, e.g. because the parameterless constructor is not
     *             present/ visible.
     * @throws AttributeBindingException if in the <code>values</code> map there are no
     *             or <code>null</code> values provided for one or more {@link Required}
     *             attributes.
     * @throws AttributeBindingException reflection-based setting or reading field values
     *             fails.
     * @throws IllegalArgumentException if <code>bindingDirectionAnnotation</code> is
     *             different from {@link Input} or {@link Output}.
     * @throws IllegalArgumentException if <code>object</code>'s type is not
     *             {@link Bindable}.
     * @throws IllegalArgumentException for debugging purposes, if an attribute field is
     *             found that is missing some of the required annotations.
     * @throws UnsupportedOperationException if an attempt is made to bind values of
     *             attributes with circular references.
     */
    public static <T> void bind(T object, Map<String, Object> values,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException, AttributeBindingException
    {
        bind(new HashSet<Object>(), object, values, bindingDirectionAnnotation,
            filteringAnnotations);
    }

    /**
     * Internal implementation with an additional parameter for tracking the already bound
     * instances.
     */
    static <T> void bind(Set<Object> boundObjects, T object, Map<String, Object> values,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException, AttributeBindingException
    {
        // Binding direction can be either @Input or @Output
        if (!Input.class.equals(bindingDirectionAnnotation)
            && !Output.class.equals(bindingDirectionAnnotation))
        {
            throw new IllegalArgumentException(
                "bindingDirectionAnnotation must either be "
                    + Input.class.getSimpleName() + " or " + Output.class.getSimpleName());
        }

        // We can only bind values on classes that are @Bindable
        if (object.getClass().getAnnotation(Bindable.class) == null)
        {
            throw new IllegalArgumentException("Class is not bindable: "
                + object.getClass().getName());
        }

        // For keeping track of circular references
        boundObjects.add(object);

        // Get all fields (including those from bindable super classes)
        final Collection<Field> fieldSet = BindableUtils
            .getFieldsFromBindableHierarchy(object.getClass());

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
                            throw new AttributeBindingException(key,
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
                        throw new AttributeBindingException(key,
                            "No value for required attribute: " + key);
                    }

                    // Try to coerce from class to its instance first
                    // Notice that if some extra annotations are provided, the newly
                    // created instance will get only those attributes bound that
                    // match any of the extra annotations.
                    if (value instanceof Class && !field.getType().equals(Class.class))
                    {
                        final Class<?> clazz = ((Class<?>) value);
                        try
                        {
                            value = clazz.newInstance();
                            if (clazz.isAnnotationPresent(Bindable.class))
                            {
                                bind(value, values, Input.class, filteringAnnotations);
                            }
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
                        final Annotation [] unmetConstraints = ConstraintValidator.isMet(
                            value, field.getAnnotations());
                        if (unmetConstraints.length > 0)
                        {
                            throw new ConstraintViolationException(key, value,
                                unmetConstraints);
                        }
                    }

                    // Finally, set the field value
                    try
                    {
                        field.setAccessible(true);
                        field.set(object, value);
                    }
                    catch (final Exception e)
                    {
                        throw new AttributeBindingException(key,
                            "Could not assign field " + object.getClass().getName() + "#"
                                + field.getName() + " with value " + value, e);
                    }
                }
                else if (Output.class.equals(bindingDirectionAnnotation)
                    && field.getAnnotation(Output.class) != null)
                {
                    // Transfer values from fields to the map here
                    try
                    {
                        field.setAccessible(true);
                        value = field.get(object);
                        values.put(key, value);
                    }
                    catch (final Exception e)
                    {
                        throw new AttributeBindingException(key,
                            "Could not get field value " + object.getClass().getName()
                                + "#" + field.getName());
                    }
                }
            }
            else
            {
                // Just get the @Bindable value to perform a recursive call on it below
                try
                {
                    field.setAccessible(true);
                    value = field.get(object);
                }
                catch (final Exception e)
                {
                    throw new AttributeBindingException(key, "Could not get field value "
                        + object.getClass().getName() + "#" + field.getName());
                }
            }

            // If value is not null and its class is @Bindable, we must descend into it
            if (value != null && value.getClass().getAnnotation(Bindable.class) != null)
            {
                // Check for circular references
                if (boundObjects.contains(value))
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
