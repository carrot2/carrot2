package org.carrot2.core.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@IsConstraint(implementator = RangeImplementator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleRange {
    double min();

    double max();
}
