/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class BoundedDoubleType extends BoundedNumberType<Double>
{
    public BoundedDoubleType(Double minValue, Double maxValue)
    {
        super(Double.class, minValue, maxValue);
    }

    @Override
    public Double valueOf(String s)
    {
        return Double.valueOf(s);
    }
}
