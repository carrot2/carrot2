/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ClassType extends AbstractType<Class>
{
    public ClassType()
    {
        super(Class.class);
    }

    public Class<?> valueOf(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException();
        }
    }
}
