
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.editors.impl.numeric;

import java.util.Map;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.workbench.editors.AttributeEditorInfo;

/**
 * Attribute editor for non-negative, limited-range double values.
 */
class DoubleRangeEditor extends NumericRangeEditorBase
{
    /**
     * Number of digits of precision.
     */
    private final static int EDITOR_PRECISION_DIGITS = 2;

    /**
     * Range constraint.
     */
    private DoubleRange constraint;

    /*
     * 
     */
    public DoubleRangeEditor()
    {
        this(EDITOR_PRECISION_DIGITS);
    }

    /*
     * 
     */
    public DoubleRangeEditor(int precisionDigits)
    {
        super(precisionDigits);
    }

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        constraint = NumberUtils.getDoubleRange(descriptor);

        final double min = constraint.min();
        final double max = constraint.max();
        final double increment = RangeUtils.getDoubleMinorTicks(min, max);
        final double pageIncrement = RangeUtils.getDoubleMajorTicks(min, max);

        setRanges(to_i(min), to_i(max), to_i(increment), to_i(pageIncrement));

        return super.init(defaultValues);
    }

    /*
     * 
     */
    @Override
    public void setValue(Object value)
    {
        if (!(value instanceof Number))
        {
            return;
        }

        super.propagateNewValue(to_i(((Number) value).doubleValue()));
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return to_d((Integer) super.getValue());
    }
}
