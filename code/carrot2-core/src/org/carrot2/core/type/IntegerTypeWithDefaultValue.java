/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class IntegerTypeWithDefaultValue extends IntegerType implements
    TypeWithDefaultValue<Integer>
{
    private final Integer defaultValue;

    public IntegerTypeWithDefaultValue(Integer defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Integer getDefaultValue()
    {
        return defaultValue;
    }
}
