package org.carrot2.core.parameters;

public class Parameter
{
    public final String name;
    public final TypeMetadata descriptor;

    public Parameter(String name, TypeMetadata descriptor)
    {
        this.name = name;
        this.descriptor = descriptor;
    }
    
    @Override
    public String toString()
    {
        return name + "=" + descriptor;
    }
}
