package org.carrot2.core.type;

public class BoundedDoubleTypeWithDefaultValue extends BoundedDoubleType implements
    TypeWithDefaultValue<Double>
{
    private final Double defaultValue;

    public BoundedDoubleTypeWithDefaultValue(Double defaultValue, Double minValue,
        Double maxValue)
    {
        super(minValue, maxValue);
        this.defaultValue = defaultValue;
    }

    public Double getDefaultValue()
    {
        return defaultValue;
    }
}
