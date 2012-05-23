
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
 * Requires that the integer attribute value is between {@link #min()} (inclusively) and
 * {@link #max()} (inclusively).
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@IsConstraint(implementation = IntRangeConstraint.class)
public @interface IntRange
{
    /**
     * Minimum value for the attribute, inclusive.
     */
    int min() default Integer.MIN_VALUE;

    /**
     * Maximum value for the attribute, inclusive.
     */
    int max() default Integer.MAX_VALUE;
}
