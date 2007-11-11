package org.carrot2.core;

import org.carrot2.core.type.Type;

public class Field
{
    public final String name;
    public final Type<?> metadata;

    public Field(String name, Type<?> metadata)
    {
        this.name = name;
        this.metadata = metadata;
    }
}
