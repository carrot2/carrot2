/**
 * 
 */
package org.carrot2.core.type;


/**
 *
 */
public abstract class BoundedNumberType<T extends Number> extends AbstractType<T>
{
    private final T minValue;
    private final T maxValue;

    public BoundedNumberType(Class<T> type, T minValue, T maxValue)
    {
        super(type);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public T getMinValue()
    {
        return minValue;
    }

    public T getMaxValue()
    {
        return maxValue;
    }
}
