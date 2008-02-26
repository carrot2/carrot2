/**
 *
 */
package carrot2.util.attribute.constraint;

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
        for (final Class<?> clazz : classes)
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
