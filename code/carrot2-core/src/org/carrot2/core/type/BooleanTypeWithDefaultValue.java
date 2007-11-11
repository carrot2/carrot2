/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class BooleanTypeWithDefaultValue extends BooleanType implements
    TypeWithDefaultValue<Boolean>
{
    private final Boolean defaultValue;

    public BooleanTypeWithDefaultValue(Boolean defaultValue)
    {
        super();
        this.defaultValue = defaultValue;
    }

    public Boolean getDefaultValue()
    {
        return defaultValue;
    }
}
