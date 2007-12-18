package org.carrot2.core.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = NumberRangeConstraint.class)
public @interface DoubleRange {
    double min();
    double max();
}
