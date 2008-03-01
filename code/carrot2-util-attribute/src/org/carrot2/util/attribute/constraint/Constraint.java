/**
 *
 */
package org.carrot2.util.attribute.constraint;

import java.lang.annotation.Annotation;

/**
 *
 */
abstract class Constraint
{
    Annotation annotation;
    
    Constraint()
    {
    }
    
    Constraint(Annotation constraintAnnotation)
    {
        this.annotation = constraintAnnotation;
    }

    abstract boolean isMet(Object value);
}
