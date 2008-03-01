package org.carrot2.util.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.ClassUtils;

public class AttributeDescriptor
{
    public final AttributeMetadata metadata;

    public final String key;
    public final Class<?> type;
    public final Object defaultValue;
    public final List<Annotation> constraints;

    public final boolean inputAttribute;
    public final boolean outputAttribute;
    public final boolean requiredAttribute;

    final Field attributeField;

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

    public Annotation getAnnotation(Class<? extends Annotation> annotationClass)
    {
        return attributeField.getAnnotation(annotationClass);
    }

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
}
