package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.attribute.constraint.*;
import org.carrot2.util.resource.Resource;

import com.google.common.collect.Sets;

/**
 * Provides methods for binding (setting and collecting) values of attributes defined by
 * the {@link Attribute} annotation.
 */
public class AttributeBinder
{
    /** Consistency checks to be applied before binding */
    private final static ConsistencyCheck [] CONSISTENCY_CHECKS =
        new ConsistencyCheck []
        {
            new ConsistencyCheckRequiredAnnotations(),
            new ConsistencyCheckNonPrimitivesWithoutConstraint()
        };

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
     * non-<code>null</code> mappings for all {@link Required} attributes that have not
     * yet been set on the <code>object</code> to a non-<code>null</code> value.
     * Otherwise an {@link AttributeBindingException} will be thrown. If the map has no
     * mapping for some non-{@link Required} attribute, the value of that attribute will
     * not be changed. However, if the map contains a <code>null</code> mapping for some
     * non-{@link Required} attribute, the value that attribute will be set to
     * <code>null</code>.
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
        AttributeBinderAction [] actions =
            new AttributeBinderAction []
            {
                new AttributeBinderActionBind(Input.class, values, true),
                new AttributeBinderActionCollect(Output.class, values),
            };

        bind(object, actions, bindingDirectionAnnotation, filteringAnnotations);
    }

    /**
     * A complementary version of the {@link #bind(Object, Map, Class, Class...)} method.
     * This method <strong>collects</strong> values of {@link Input} attributes and
     * <strong>sets</strong> values of {@link Output} attributes.
     */
    public static <T> void unbind(T object, Map<String, Object> values,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException, AttributeBindingException
    {
        AttributeBinderAction [] actions =
            new AttributeBinderAction []
            {
                new AttributeBinderActionCollect(Input.class, values),
                new AttributeBinderActionBind(Output.class, values, true),
            };

        bind(object, actions, bindingDirectionAnnotation, filteringAnnotations);
    }

    /**
     * A more flexible version of {@link #bind(Object, Map, Class, Class...)} that accepts
     * custom {@link AttributeBinderAction}s. For experts only.
     */
    public static <T> void bind(T object,
        AttributeBinderAction [] attributeBinderActions,
        Class<? extends Annotation> bindingDirectionAnnotation,
        Class<? extends Annotation>... filteringAnnotations)
        throws InstantiationException, AttributeBindingException
    {
        bind(new HashSet<Object>(), object, attributeBinderActions,
            bindingDirectionAnnotation, filteringAnnotations);
    }

    /**
     * Internal implementation that tracks object that have already been bound.
     */
    static <T> void bind(Set<Object> boundObjects, T object,
        AttributeBinderAction [] attributeBinderActions,
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
        final Collection<Field> fieldSet =
            BindableUtils.getFieldsFromBindableHierarchy(object.getClass());

        for (final Field field : fieldSet)
        {
            final String key = BindableUtils.getKey(field);
            Object value = null;

            // Get the @Bindable value to perform a recursive call on it later on
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

            // Apply consistency checks
            boolean consistent = true;
            for (int i = 0; consistent && i < CONSISTENCY_CHECKS.length; i++)
            {
                consistent &=
                    CONSISTENCY_CHECKS[i].check(field, bindingDirectionAnnotation,
                        filteringAnnotations);
            }

            // We skip fields that do not have all the required annotations
            if (consistent)
            {
                // Apply binding actions provided
                for (int i = 0; i < attributeBinderActions.length; i++)
                {
                    attributeBinderActions[i].performAction(object, key, field, value,
                        bindingDirectionAnnotation, filteringAnnotations);
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
                bind(boundObjects, value, attributeBinderActions,
                    bindingDirectionAnnotation, filteringAnnotations);
            }
        }
    }

    /**
     * An action to be applied during attribute binding.
     */
    public static interface AttributeBinderAction
    {
        public <T> void performAction(T object, String key, Field field, Object value,
            Class<? extends Annotation> bindingDirectionAnnotation,
            Class<? extends Annotation>... filteringAnnotations)
            throws InstantiationException;
    }

    /**
     * Transforms attribute values.
     */
    public static interface AttributeTransformer
    {
        public Object transform(Object value, String key, Field field,
            Class<? extends Annotation> bindingDirectionAnnotation,
            Class<? extends Annotation>... filteringAnnotations);
    }

    /**
     * An action that binds all {@link Input} attributes.
     */
    public static class AttributeBinderActionBind implements AttributeBinderAction
    {
        final private Map<String, Object> values;
        final private Class<?> bindingDirectionAnnotation;
        final boolean checkRequired;

        public AttributeBinderActionBind(Class<?> bindingDirectionAnnotation,
            Map<String, Object> values, boolean checkRequired)
        {
            this.values = values;
            this.bindingDirectionAnnotation = bindingDirectionAnnotation;
            this.checkRequired = checkRequired;
        }

        public <T> void performAction(T object, String key, Field field, Object value,
            Class<? extends Annotation> bindingDirectionAnnotation,
            Class<? extends Annotation>... filteringAnnotations)
            throws InstantiationException
        {
            if (this.bindingDirectionAnnotation.equals(bindingDirectionAnnotation)
                && field.getAnnotation(bindingDirectionAnnotation) != null)
            {
                final boolean required =
                    field.getAnnotation(Required.class) != null && checkRequired;
                final Object currentValue = value;

                // Transfer values from the map to the fields. If the input map
                // doesn't contain an entry for this key, do nothing. Otherwise,
                // perform binding as usual. This will allow to set null values
                if (!values.containsKey(key))
                {
                    if (currentValue == null && required)
                    {
                        // Throw exception only if the current value is null
                        throw new AttributeBindingException(key,
                            "No value for required attribute: " + key);
                    }
                    return;
                }

                // Note that the value can still be null here
                value = values.get(key);

                if (required)
                {
                    if (value == null)
                    {
                        throw new AttributeBindingException(key,
                            "Not allowed to set required attribute to null: " + key);
                    }
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
                    final Annotation [] unmetConstraints =
                        ConstraintValidator.isMet(value, field.getAnnotations());
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
                    throw new AttributeBindingException(key, "Could not assign field "
                        + object.getClass().getName() + "#" + field.getName()
                        + " with value " + value, e);
                }
            }
        }
    }

    /**
     * An action that binds all {@link Output} attributes.
     */
    public static class AttributeBinderActionCollect implements AttributeBinderAction
    {
        final private Map<String, Object> values;
        final private Class<?> bindingDirectionAnnotation;
        final AttributeTransformer [] transformers;

        public AttributeBinderActionCollect(Class<?> bindingDirectionAnnotation,
            Map<String, Object> values, AttributeTransformer... transformers)
        {
            this.values = values;
            this.bindingDirectionAnnotation = bindingDirectionAnnotation;
            this.transformers = transformers;
        }

        public <T> void performAction(T object, String key, Field field, Object value,
            Class<? extends Annotation> bindingDirectionAnnotation,
            Class<? extends Annotation>... filteringAnnotations)
            throws InstantiationException
        {
            if (this.bindingDirectionAnnotation.equals(bindingDirectionAnnotation)
                && field.getAnnotation(bindingDirectionAnnotation) != null)
            {
                try
                {
                    field.setAccessible(true);

                    // Apply transforms
                    for (AttributeTransformer transformer : transformers)
                    {
                        value =
                            transformer.transform(value, key, field,
                                bindingDirectionAnnotation, filteringAnnotations);
                    }

                    values.put(key, value);
                }
                catch (final Exception e)
                {
                    throw new AttributeBindingException(key, "Could not get field value "
                        + object.getClass().getName() + "#" + field.getName());
                }
            }
        }
    }

    /**
     * Checks individual attribute definitions for consistency, e.g. whether they have all
     * required annotations.
     */
    static abstract class ConsistencyCheck
    {
        /**
         * Checks an attribute's annotations.
         * 
         * @param bindingDirection
         * @return <code>true</code> if the attribute passed the check and can be bound,
         *         <code>false</code> if the attribute did not pass the check and cannot
         *         be bound.
         * @throws IllegalArgumentException when attribute's annotations are inconsistent
         */
        abstract boolean check(Field field, Class<? extends Annotation> bindingDirection,
            Class<? extends Annotation>... filteringAnnotations);
    }

    /**
     * Checks if all required attribute annotations are provided.
     */
    static class ConsistencyCheckRequiredAnnotations extends ConsistencyCheck
    {
        @Override
        boolean check(Field field, Class<? extends Annotation> bindingDirection,
            Class<? extends Annotation>... filteringAnnotations)
        {
            final boolean hasAttribute = field.getAnnotation(Attribute.class) != null;
            boolean hasBindingDirection =
                field.getAnnotation(Input.class) != null
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
                        "Binding time or direction defined for a field ("
                            + field.getClass() + "#" + field.getName()
                            + ") that does not have an @"
                            + Attribute.class.getSimpleName() + " annotation");
                }
            }

            return hasAttribute && hasExtraAnnotations;
        }
    }

    /**
     * Checks whether attributes of non-primitive types have the
     * {@link ImplementingClasses} constraint.
     */
    static class ConsistencyCheckNonPrimitivesWithoutConstraint extends ConsistencyCheck
    {
        static Set<Class<?>> ALLOWED_PLAIN_TYPES =
            Sets.<Class<?>> immutableSet(Byte.class, Short.class, Integer.class,
                Long.class, Float.class, Double.class, Boolean.class, String.class,
                Class.class, Resource.class, Collection.class, Map.class);

        static Set<Class<?>> ALLOWED_ASSIGNABLE_TYPES =
            Sets.<Class<?>> immutableSet(Enum.class, Resource.class, Collection.class,
                Map.class);

        @Override
        boolean check(Field field, Class<? extends Annotation> bindingDirection,
            Class<? extends Annotation>... filteringAnnotations)
        {
            if (!Input.class.equals(bindingDirection))
            {
                return true;
            }

            final Class<?> attributeType = ClassUtils.primitiveToWrapper(field.getType());

            if (!ALLOWED_PLAIN_TYPES.contains(attributeType)
                && !isAllowedAssignableType(attributeType) && !isConstrained(field))
            {
                throw new IllegalArgumentException("Non-primitive typed attribute "
                    + field.getDeclaringClass().getName() + "#" + field.getName()
                    + " must have some constraint specified.");
            }

            return true;
        }

        private static boolean isConstrained(Field field)
        {
            for (int i = 0; i < field.getAnnotations().length; i++)
            {
                Annotation ann = field.getAnnotations()[i];
                if (ann.annotationType().isAnnotationPresent(IsConstraint.class))
                {
                    return true;
                }
            }
            return false;
        }

        private static boolean isAllowedAssignableType(Class<?> attributeType)
        {
            for (Class<?> clazz : ALLOWED_ASSIGNABLE_TYPES)
            {
                if (clazz.isAssignableFrom(attributeType))
                {
                    return true;
                }
            }

            return false;
        }
    }
}
