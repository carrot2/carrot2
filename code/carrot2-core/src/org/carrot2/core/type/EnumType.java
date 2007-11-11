/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class EnumType<T extends Enum<T>> extends AbstractType<T>
{
    public EnumType(Class<T> type)
    {
        super(type);
    }

    public T valueOf(String s)
    {
        return Enum.valueOf(getType(), s);
    }
}
