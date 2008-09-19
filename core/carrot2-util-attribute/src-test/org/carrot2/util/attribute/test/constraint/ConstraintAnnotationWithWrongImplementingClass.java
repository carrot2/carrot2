package org.carrot2.util.attribute.test.constraint;

import java.lang.annotation.*;

import org.carrot2.util.attribute.constraint.IsConstraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = String.class)
public @interface ConstraintAnnotationWithWrongImplementingClass
{
}
