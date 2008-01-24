package org.carrot2.core.parameter;

import java.lang.reflect.Field;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.carrot2.core.constraint.Constraint;

public class AttributeDescriptor
{
    public final String key;
    public final Class<?> type;
    public final Object defaultValue;
    public final Constraint constraint;
    final Field field;

    AttributeDescriptor(String name, Object defaultValue, Constraint constraint,
        Field field)
    {
        if (name == null || field == null)
        {
            throw new IllegalArgumentException();
        }

        this.key = name;
        this.field = field;
        this.type = ClassUtils.primitiveToWrapper(field.getType());
        this.defaultValue = defaultValue;
        this.constraint = constraint;
    }

    public String getKey()
    {
        return key;
    }

    public Class<?> getType()
    {
        return type;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

    public Constraint getConstraint()
    {
        return constraint;
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
            && ObjectUtils.equals(other.constraint, this.constraint);
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
