
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
