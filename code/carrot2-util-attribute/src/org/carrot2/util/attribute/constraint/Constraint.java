package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

/**
 * Implementation of a constraint.
 */
abstract class Constraint
{
    /** Annotation corresponding to this constraint */
    Annotation annotation;

    Constraint()
    {
    }

    Constraint(Annotation constraintAnnotation)
    {
        this.annotation = constraintAnnotation;
    }

    /**
     * Checks if the provided <code>value</code> meets this constraint.
     */
    abstract boolean isMet(Object value);
}
