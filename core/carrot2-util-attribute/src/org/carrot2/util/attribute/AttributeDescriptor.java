/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
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

import org.apache.commons.lang.ClassUtils;
import org.carrot2.util.ListUtils;
import org.carrot2.util.attribute.constraint.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Persist;

import com.google.common.collect.Lists;

/**
 * Provides a full description of an individual attribute, including its {@link #key},
 * {@link #type}, {@link #defaultValue} and {@link #constraints}. Also contains
 * human-readable {@link #metadata} about the attribute such as title, label or
 * description.
 * <p>
 * {@link AttributeDescriptor}s can be obtained from {@link BindableDescriptor}s, which in
 * turn are built by {@link BindableDescriptorBuilder#buildDescriptor(Object)};
 */
@Root(name = "attribute-descriptor")
public class AttributeDescriptor
{
    /**
     * Human-readable metadata describing the attribute.
     */
    @Element
    public final AttributeMetadata metadata;

    /**
     * Type of the attribute as defined by {@link Attribute#key()}.
     */
    @org.simpleframework.xml.Attribute
    public final String key;

    /**
     * Type of the attribute. Primitive types are represented by their corresponding
     * wrapper/ box types.
     */
    @org.simpleframework.xml.Attribute
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
    @org.simpleframework.xml.Attribute(name = "required")
    public final boolean requiredAttribute;

    /**
     * Field representing the attribute.
     */
    final Field attributeField;

    /**
     * Name of field representing the attribute, for serialization.
     */
    @org.simpleframework.xml.Attribute(name = "field")
    @SuppressWarnings("unused")
    private String attributeFieldString;

    /**
     * Default value as string, for serialization.
     */
    @org.simpleframework.xml.Attribute(name = "default", required = false)
    @SuppressWarnings("unused")
    private String defaultValueString;

    /**
     * Annotation names, for serialization.
     */
    @ElementList(entry = "annotation", required = false)
    private ArrayList<String> annotations;

    /**
     * Instances of this attribute's constraints, for serialization.
     */
    @ElementList(name = "constraints", required = false)
    @SuppressWarnings("unused")
    private ArrayList<Constraint> constraintInstances;

    /**
     * In case of Enum attributes, a list of allowed values, for serialization.
     */
    @ElementList(name = "allowed-values", entry = "value", required = false)
    private ArrayList<AllowedValue> allowedValues;

    /**
     * 
     */
    AttributeDescriptor(Field field, Object defaultValue, List<Annotation> constraints,
        AttributeMetadata metadata)
    {
        this.attributeField = field;

        this.key = BindableUtils.getKey(field);
        this.type = ClassUtils.primitiveToWrapper(field.getType());
        this.defaultValue = defaultValue;
        this.constraints = constraints;
        this.metadata = metadata;

        this.inputAttribute = field.getAnnotation(Input.class) != null;
        this.outputAttribute = field.getAnnotation(Output.class) != null;
        this.requiredAttribute = field.getAnnotation(Required.class) != null;
    }

    /**
     * Returns an annotation specified for the attribute.
     * 
     * @param annotationClass type of annotation to be returned
     * @return annotation of the attribute or <code>null</code> is annotation of the
     *         provided type is not defined for the attribute
     */
    public Annotation getAnnotation(Class<? extends Annotation> annotationClass)
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

    @Override
    public String toString()
    {
        return key + "=" + type;
    }

    @Persist
    @SuppressWarnings(
    {
        "unused", "unchecked"
    })
    private void beforeSerialization()
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
                if (defaultValue instanceof Enum)
                {
                    defaultValueString = ((Enum) defaultValue).name();
                }
                else
                {
                    defaultValueString = defaultValue.toString();
                }
            }
        }

        // Annotations as strings
        final Annotation [] annotationInstances = attributeField.getAnnotations();
        annotations = Lists.newArrayListWithExpectedSize(annotationInstances.length);
        for (Annotation annotation : annotationInstances)
        {
            annotations.add(annotation.annotationType().getSimpleName());
        }

        // Constraints
        constraintInstances = ListUtils.asArrayList(ConstraintFactory
            .createConstraints(attributeField.getAnnotations()));

        // Allowed values of enum types
        if (type.isEnum())
        {
            allowedValues = Lists.newArrayList();
            final Enum<?> [] enumConstants = ((Class<Enum<?>>) type).getEnumConstants();
            for (Enum<?> object : enumConstants)
            {
                allowedValues.add(new AllowedValue(object.name(), object.toString()));
            }
        }
    }

    private static class AllowedValue
    {
        @org.simpleframework.xml.Attribute
        String label;

        @Text
        String value;

        public AllowedValue(String value, String label)
        {
            this.value = value;
            this.label = label;
        }
    }
}
