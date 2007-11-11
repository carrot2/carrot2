/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class TypeBuilder
{
    public static BoundedIntegerType build(int minValue, int maxValue)
    {
        return new BoundedIntegerType(minValue, maxValue);
    }

    public static BoundedIntegerTypeWithDefaultValue build(int defaultValue,
        int minValue, int maxValue)
    {
        return new BoundedIntegerTypeWithDefaultValue(defaultValue, minValue, maxValue);
    }

    public static BoundedDoubleType build(double minValue, double maxValue)
    {
        return new BoundedDoubleType(minValue, maxValue);
    }

    public static BoundedDoubleTypeWithDefaultValue build(double defaultValue,
        double minValue, double maxValue)
    {
        return new BoundedDoubleTypeWithDefaultValue(defaultValue, minValue, maxValue);
    }
}
