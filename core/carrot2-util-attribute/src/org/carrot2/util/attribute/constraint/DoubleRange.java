package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;


/**
 * Requires that the double attribute value is between {@link #min()} (inclusively) and
 * {@link #max()} (inclusively).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = DoubleRangeConstraint.class)
public @interface DoubleRange
{
    /**
     * Minimum value for the attribute, inclusive.
     */
    double min() default Double.NEGATIVE_INFINITY;

    /**
     * Maximum value for the attribute, inclusive.
     */
    double max() default Double.POSITIVE_INFINITY;
}
