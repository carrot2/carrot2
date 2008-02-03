package org.carrot2.core.attribute;

import java.lang.reflect.Field;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.carrot2.core.constraint.Constraint;

public class AttributeDescriptor
{
    public final AttributeMetadata metadata;

    public final String key;
    public final Class<?> type;
    public final Object defaultValue;
    public final Constraint constraint;

    AttributeDescriptor(Field field, Object defaultValue, Constraint constraint,
        AttributeMetadata metadata)
    {
        this(BindableUtils.getKey(field), ClassUtils.primitiveToWrapper(field.getType()),
            defaultValue, constraint, metadata);
    }

    AttributeDescriptor(String key, Class<?> type, Object defaultValue,
        Constraint constraint, AttributeMetadata metadata)
    {
        if (key == null || type == null)
        {
            throw new IllegalArgumentException();
        }

        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.metadata = metadata;
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

        AttributeDescriptor other = ((AttributeDescriptor) obj);
        return other.key.equals(this.key) && other.type.equals(this.type)
            && ObjectUtils.equals(defaultValue, defaultValue)
            && ObjectUtils.equals(other.constraint, this.constraint)
            && ObjectUtils.equals(metadata, other.metadata);
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
