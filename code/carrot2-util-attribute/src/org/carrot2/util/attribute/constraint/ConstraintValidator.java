/**
 * 
 */
package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.collect.Lists;

/**
 *
 */
public class ConstraintValidator
{
    public static Annotation [] isMet(Object value, Annotation... constraintAnnotations)
    {
        final List<Constraint> constraints = ConstraintFactory
            .createConstraints(constraintAnnotations);
        final List<Annotation> unmetConstraints = Lists.newArrayList();

        for (Constraint constraint : constraints)
        {
            if (!constraint.isMet(value))
            {
                unmetConstraints.add(constraint.annotation);
            }
        }

        return unmetConstraints.toArray(new Annotation [unmetConstraints.size()]);
    }
}
