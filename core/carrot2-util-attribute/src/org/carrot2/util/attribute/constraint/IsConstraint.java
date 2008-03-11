package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * Marks annotations as annotation constraints.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface IsConstraint
{
    /**
     * Points to the class implementing the constraint.
     */
    Class<?> implementation();
}
