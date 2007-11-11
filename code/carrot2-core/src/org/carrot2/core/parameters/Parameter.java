package org.carrot2.core.parameters;

import org.carrot2.core.type.Type;

public class Parameter
{
    public final String name;
    public final Type<?> type;

    public Parameter(String id, Type<?> type)
    {
        this.name = id;
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
