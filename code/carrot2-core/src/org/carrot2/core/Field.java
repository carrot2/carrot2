package org.carrot2.core;

import org.carrot2.core.parameters.TypeMetadata;

public class Field
{
    public final String name;
    public final TypeMetadata metadata;

    public Field(String name, TypeMetadata metadata)
    {
        this.name = name;
        this.metadata = metadata;
    }
}
