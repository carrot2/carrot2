/**
 * 
 */
package org.carrot2.core.constraint;

/**
 *
 */
public class ImplementingClassesConstraint implements Constraint
{
    private Class<?> [] classes;

    public ImplementingClassesConstraint()
    {
    }
    
    public ImplementingClassesConstraint(Class<?> [] classes)
    {
        this.classes = classes;
    }

    @Override
    public boolean isMet(Object value)
    {
        for (Class<?> clazz : classes)
        {
            if (clazz.equals(value.getClass()))
            {
                return true;
            }
        }

        return false;
    }

    public Class<?> [] getClasses()
    {
        return classes;
    }
}
