
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

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persist;

/**
 * Implementation of the {@link IntRangeConstraint}.
 */
@Root(name = "int-range")
class IntRangeConstraint extends Constraint
{
    Integer min;
    Integer max;

    /**
     * For serialization.
     */
    @Attribute(name = "min", required = false)
    Integer minOrNull;

    /**
     * For serialization.
     */
    @Attribute(name = "max", required = false)
    Integer maxOrNull;

    protected boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        checkAssignableFrom(value, Byte.class, Short.class, Integer.class, AtomicInteger.class);

        final Integer v = ((Number) value).intValue();
        return v >= min && v <= max;
    }

    @Override
    public String toString()
    {
        return "range(min = " + min.toString() + ", max = " + max.toString() + ")";
    }

    @Override
    public void populateCustom(Annotation annotation)
    {
        final IntRange range = (IntRange) annotation;
        this.min = range.min();
        this.max = range.max();
    }

    @Persist
    void beforeSerialization()
    {
        if (min > Integer.MIN_VALUE)
        {
            minOrNull = min;
        }

        if (max < Integer.MAX_VALUE)
        {
            maxOrNull = max;
        }
    }
}
