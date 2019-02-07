
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.carrot2.util.attribute.constraint.*;
import org.carrot2.util.attribute.constraint.Constraint;

/**
 * Provides a full description of an individual attribute, including its {@link #key},
 * {@link #type}, {@link #defaultValue} and {@link #constraints}. Also contains
 * human-readable {@link #metadata} about the attribute such as title, label or
 * description.
 * <p>
 * {@link AttributeDescriptor}s can be obtained from {@link BindableDescriptor}s, which in
 * turn are built by {@link BindableDescriptorBuilder#buildDescriptor(Object)};
 */
public class AttributeDescriptor
{
    /**
     * Human-readable metadata describing the attribute.
     */
    public final AttributeMetadata metadata;

    /**
     * Type of the attribute as defined by {@link Attribute#key()}.
     */
    public final String key;

    /**
     * Type of the attribute. Primitive types are represented by their corresponding
     * wrapper/ box types.
     */
    public final Class<?> type;

    /**
     * Default value of the attribute.
     */
    public final Object defaultValue;

    /**
     * Constraints defined for the attribute. If the attribute has no constraints, this
     * list is empty.
     */
    public final List<Annotation> constraints;

    /**
     * <code>True</code> if the attribute is an {@link Input} attribute.
     */
    public final boolean inputAttribute;

    /**
     * <code>True</code> if the attribute is an {@link Output} attribute.
     */
    public final boolean outputAttribute;

    /**
     * <code>True</code> if the attribute is a {@link Required} attribute.
     */
    public final boolean requiredAttribute;

    /**
     * Field representing the attribute.
     */
    final Field attributeField;

    /**
     * Name of field representing the attribute, for serialization.
     */
    private String attributeFieldString;

    /**
     * Name of the class declaring the attribute, for serialization.
     */
    private String attributeDeclaringClassString;
    
    /**
     * Default value as string, for serialization.
     */
    private String defaultValueString;

    /**
     * Annotation names, for serialization.
     */
    private ArrayList<String> annotations;

    /**
     * Instances of this attribute's constraints, for serialization.
     */
    private List<Constraint> constraintInstances;

    /**
     * In case of Enum attributes, a list of allowed values, for serialization.
     */
    private AllowedValues allowedValues;

    /**
     * 
     */
    AttributeDescriptor(Field field, Object defaultValue, List<Annotation> constraints,
        AttributeMetadata metadata)
    {
        this.attributeField = field;
        this.attributeDeclaringClassString = field.getDeclaringClass().getName();

        this.key = BindableUtils.getKey(field);
        this.type = AttributeUtils.toBoxed(field.getType());
        this.defaultValue = defaultValue;
        this.constraints = constraints;
        this.metadata = metadata;

        this.inputAttribute = field.getAnnotation(Input.class) != null;
        this.outputAttribute = field.getAnnotation(Output.class) != null;
        this.requiredAttribute = field.getAnnotation(Required.class) != null;

        prepareForSerialization();
    }

    /**
     * Returns an annotation specified for the attribute.
     * 
     * @param annotationClass type of annotation to be returned
     * @return annotation of the attribute or <code>null</code> is annotation of the
     *         provided type is not defined for the attribute
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
    {
        return attributeField.getAnnotation(annotationClass);
    }

    /**
     * Returns <code>true</code> if the given value is valid for the attribute described
     * by this descriptor (non-<code>null</code> for {@link Required} attributes and
     * fulfilling all other constraints).
     */
    public final boolean isValid(Object value)
    {
        if (requiredAttribute && value == null)
        {
            return false;
        }

        if (value == null)
        {
            value = defaultValue;
        }

        final Annotation [] constraints = this.constraints
            .toArray(new Annotation [this.constraints.size()]);

        return ConstraintValidator.isMet(value, constraints).length == 0;
    }

    /**
     * Transforms {@link AttributeDescriptor}s into their keys.
     */
    public static final class AttributeDescriptorToKey implements Function<AttributeDescriptor, String>
    {
        public static final AttributeDescriptorToKey INSTANCE = new AttributeDescriptorToKey();

        private AttributeDescriptorToKey()
        {
        }

        public String apply(AttributeDescriptor d)
        {
            return d.key;
        }
    }

    @Override
    public String toString()
    {
        return key + "=" + type;
    }

    /**
     * @see "http://issues.carrot2.org/browse/CARROT-693"
     */
    @SuppressWarnings("unchecked")
    private void prepareForSerialization()
    {
        attributeFieldString = attributeField.getName();

        // Default value
        if (inputAttribute && defaultValue != null)
        {
            if (defaultValue instanceof Collection<?>)
            {
                if (((Collection<?>) defaultValue).isEmpty())
                {
                    return;
                }
            }

            if (attributeField.getAnnotation(ImplementingClasses.class) != null)
            {
                defaultValueString = defaultValue.getClass().getName();
            }
            else
            {
                if (defaultValue instanceof Enum<?>)
                {
                    defaultValueString = ((Enum<?>) defaultValue).name();
                }
                else
                {
                    defaultValueString = defaultValue.toString();
                }
            }
        }

        // Annotations as strings
        final Annotation [] annotationInstances = attributeField.getAnnotations();
        annotations = new ArrayList<>(annotationInstances.length);
        for (Annotation annotation : annotationInstances)
        {
            annotations.add(annotation.annotationType().getSimpleName());
        }

        // Constraints. As ValueHintEnumConstraint is a dummy constraint whose main
        // purpose is to expose the ValueHintEnum annotation in the API, we filter it out
        // here and put the values provided by ValueHintEnum in the allowed values list in
        // the same way as for proper enums.
        constraintInstances = ConstraintFactory.createConstraints(attributeField.getAnnotations())
            .stream()
            .filter(constraint -> !(constraint instanceof ValueHintEnumConstraint))
            .collect(Collectors.toList());

        // Remove empty element from serialized output
        if (constraintInstances.isEmpty())
        {
            constraintInstances = null;
        }

        // Allowed values of enum types and strings with ValueHintEnum annotations
        Class<? extends Enum<?>> enumType = null;
        boolean otherValuesAllowed = false;
        if (type.isEnum())
        {
            enumType = (Class<? extends Enum<?>>) type;
            otherValuesAllowed = false;
        }
        else if (CharSequence.class.isAssignableFrom(type))
        {
            final ValueHintEnum hint = getAnnotation(ValueHintEnum.class);
            if (hint != null)
            {
                enumType = hint.values();
                otherValuesAllowed = true;
            }
        }

        if (enumType != null)
        {
            allowedValues = new AllowedValues(otherValuesAllowed);
            final Map<String, String> valueToLabel = ValueHintMappingUtils.getValueToFriendlyName(enumType);

            for (String value : ValueHintMappingUtils.getValidValuesMap(enumType).keySet())
            {
                allowedValues.add(value, valueToLabel.get(value));
            }
        }
    }

    private static class AllowedValues
    {
        private ArrayList<AllowedValue> allowedValues;
        private boolean otherValuesAllowed;

        private AllowedValues(boolean otherValuesAllowed)
        {
            this.allowedValues = new ArrayList<>();
            this.otherValuesAllowed = otherValuesAllowed;
        }

        void add(String value, String label)
        {
            allowedValues.add(new AllowedValue(value, label));
        }
    }

    private static class AllowedValue
    {
        String label;
        String value;

        public AllowedValue(String value, String label)
        {
            this.value = value;
            this.label = label;
        }
    }
}
