package org.carrot2.util.attribute.constraint;

import java.lang.annotation.*;

/**
 * Requires that the double attribute value is between {@link #min()} (inclusively) and
 * {@link #max()} (inclusively).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = RangeConstraint.class)
public @interface DoubleRange
{
    /**
     * Minimum value for the attribute, inclusive.
     */
    double min();

    /**
     * Maximum value for the attribute, inclusive.
     */
    double max();
}
