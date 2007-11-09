package org.carrot2.core.parameters;

public class IntegerTypeMetadata extends TypeMetadata
{
    int min;
    int max;

    public IntegerTypeMetadata(int min, int max)
    {
        this.min = min;
        this.max = max;
    }
}
