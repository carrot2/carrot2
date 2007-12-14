package org.carrot2.core.parameters;

import org.carrot2.core.constraints.Constraint;
import org.carrot2.util.ObjectUtils;

public class ParameterDescriptor
{
    public final String name;
    public final Class<?> type;
    public final Object defaultValue;
    public final Constraint<?> constraint;

    public ParameterDescriptor(String name, Class<?> type, Object defaultValue, Constraint<?> constraint)
    {
        if (name == null || type == null)
        {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
    }

    public String getName()
    {
        return name;
    }

    public Class<?> getType()
    {
        return type;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    public Constraint<?> getConstraint()
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

        if (obj == null || !(obj instanceof ParameterDescriptor))
        {
            return false;
        }

        ParameterDescriptor other = ((ParameterDescriptor) obj);
        return other.name.equals(this.name)
            && other.type.equals(this.type)
            && ObjectUtils.equals(defaultValue, defaultValue)
            && ObjectUtils.equals(other.constraint, this.constraint);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return name + "=" + type;
    }
}
