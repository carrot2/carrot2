package org.carrot2.util.attribute.test.constraint;

import java.lang.annotation.*;

import org.carrot2.util.attribute.constraint.IsConstraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = TestConstraint2Constraint.class)
public @interface TestConstraint2
{
    int value();
}
