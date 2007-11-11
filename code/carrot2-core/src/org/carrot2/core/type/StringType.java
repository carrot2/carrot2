/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public class StringType extends AbstractType<String>
{
    public StringType()
    {
        super(String.class);
    }

    @Override
    public String valueOf(String s)
    {
        return s;
    }

}
