package org.carrot2.util.attribute.test.constraint;

import java.lang.annotation.*;

import org.carrot2.util.attribute.constraint.IsConstraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = TestConstraint1Constraint.class)
public @interface TestConstraint1
{
    int value();
}
