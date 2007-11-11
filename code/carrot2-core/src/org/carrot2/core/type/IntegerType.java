/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class IntegerType extends AbstractType<Integer>
{
    public IntegerType()
    {
        super(Integer.class);
    }

    @Override
    public Integer valueOf(String s)
    {
        return Integer.valueOf(s);
    }
}
