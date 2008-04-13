package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

/**
 * Provides a full description of an individual attribute, including its {@link #key},
 * {@link #type}, {@link #defaultValue} and {@link #constraints}. Also contains
 * human-readable {@link #metadata} about the attribute such as title, label or
 * description.
 * <p>
 * {@link AttributeDescriptor}s can be obtained from {@link BindableDescriptor}s, which
 * in turn are built by {@link BindableDescriptorBuilder#buildDescriptor(Object)};
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
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof AttributeDescriptor))
        {
            return false;
        }

        final AttributeDescriptor other = ((AttributeDescriptor) obj);
        return other.attributeField.equals(this.attributeField);
    }

    @Override
    public int hashCode()
    {
        return key.hashCode();
    }

    @Override
    public String toString()
    {
        return key + "=" + type;
    }
    */
}
