/**
 *
 */
package carrot2.util.attribute.constraint;

import java.util.Set;

/**
 * Utility methods for working with {@link Constraint}s.
 */
public class ConstraintUtils
{
    /**
     * Extracts the array of allowed implementation classes encoded in the provided
     * <code>constraint</code>.
     *
     * @param constraint the constraint to extract implementation classes from. Only
     *            {@link ImplementingClassesConstraint} and {@link CompoundConstraint} are
     *            processed, the other constraints are ignored as they do not carry the
     *            required information.
     * @return an array of allowed implementing classes or <code>null</code> if such
     *         information is not available in the provided <code>constraint</code>.
     */
    public static Class<?> [] getImplementingClasses(Constraint constraint)
    {
        if (constraint instanceof ImplementingClassesConstraint)
        {
            return ((ImplementingClassesConstraint) constraint).getClasses();
        }
        else if (constraint instanceof CompoundConstraint)
        {
            final Set<Constraint> constraints = ((CompoundConstraint) constraint)
                .getConstraints();
            for (final Constraint partialConstraint : constraints)
            {
                final Class<?> [] implementingClasses = getImplementingClasses(partialConstraint);
                if (implementingClasses != null)
                {
                    return implementingClasses;
                }
            }
        }

        return null;
    }
}
