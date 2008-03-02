package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = String.class)
public @interface TestConstraintAnnotation
{
}
