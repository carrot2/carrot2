/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class BoundedIntegerType extends BoundedNumberType<Integer>
{
    public BoundedIntegerType(Integer minValue, Integer maxValue)
    {
        super(Integer.class, minValue, maxValue);
    }

    @Override
    public Integer valueOf(String s)
    {
        return Integer.valueOf(s);
    }
}
