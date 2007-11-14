package org.carrot2.core.parameters;

import org.carrot2.core.type.Type;
import org.carrot2.core.type.TypeWithDefaultValue;

public class Parameter
{
    public final String name;

    /**
     * TODO: assuming all parameters are optional, we should use
     * {@link TypeWithDefaultValue} here instad of the unrestricted {@link Type}.
     */
    public final Type<?> type;

    public Parameter(String name, Type<?> type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Type<?> getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return name + "=" + type.getType();
    }
}
