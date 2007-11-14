/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class EnumTypeWithDefaultValue<T extends Enum<T>> extends EnumType<T> implements
    TypeWithDefaultValue<T>
{
    public final T defaultValue;
    
    public EnumTypeWithDefaultValue(Class<T> type, T defaultValue)
    {
        super(type);
        this.defaultValue = defaultValue;
    }

    public T valueOf(String s)
    {
        return Enum.valueOf(getType(), s);
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }
}
