
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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persist;

/**
 * Implementation of the {@link DoubleRangeConstraint}.
 */
@Root(name = "double-range")
class DoubleRangeConstraint extends Constraint
{
    Double min;
    Double max;

    /**
     * For serialization.
     */
    @Attribute(name = "min", required = false)
    Double minOrNull;

    /**
     * For serialization.
     */
    @Attribute(name = "max", required = false)
    Double maxOrNull;

    protected boolean isMet(Object value)
    {
        if (value == null)
        {
            return false;
        }

        checkAssignableFrom(value, Number.class);

        final double v = ((Number) value).doubleValue();
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
        final DoubleRange range = (DoubleRange) annotation;
        this.min = range.min();
        this.max = range.max();
    }

    @Persist
    void beforeSerialization()
    {
        if (min > Double.NEGATIVE_INFINITY)
        {
            minOrNull = min;
        }

        if (max < Double.POSITIVE_INFINITY)
        {
            maxOrNull = max;
        }
    }
}
