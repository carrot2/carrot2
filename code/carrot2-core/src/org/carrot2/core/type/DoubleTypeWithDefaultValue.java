/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class DoubleTypeWithDefaultValue extends DoubleType implements
    TypeWithDefaultValue<Double>
{
    private Double defaultValue;

    public DoubleTypeWithDefaultValue(Double defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Double getDefaultValue()
    {
        return defaultValue;
    }
}
