/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class DoubleType extends AbstractType<Double>
{
    public DoubleType()
    {
        super(Double.class);
    }

    @Override
    public Double valueOf(String s)
    {
        return Double.valueOf(s);
    }
}
