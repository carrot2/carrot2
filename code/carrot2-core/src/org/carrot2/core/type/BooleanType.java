/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class BooleanType extends AbstractType<Boolean>
{
    public BooleanType()
    {
        super(Boolean.class);
    }

    @Override
    public Boolean valueOf(String s)
    {
        return Boolean.valueOf(s);
    }
}
