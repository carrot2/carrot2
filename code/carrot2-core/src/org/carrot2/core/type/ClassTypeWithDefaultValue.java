/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ClassTypeWithDefaultValue extends ClassType implements
    TypeWithDefaultValue<Class>
{
    private Class defaultValue;

    public ClassTypeWithDefaultValue(Class defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Class getDefaultValue()
    {
        return defaultValue;
    }
}
