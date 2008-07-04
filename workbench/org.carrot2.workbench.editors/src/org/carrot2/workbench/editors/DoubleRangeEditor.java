package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;

/**
 * Attribute editor for double values, possibly with {@link DoubleRange} annotations.
 */
public final class DoubleRangeEditor extends NumericRangeEditorBase
{
    /**
     * Number of digits of precision.
     */
    private final static int EDITOR_PRECISION_DIGITS = 2;

    /**
     * Range constraint or <code>null</code> if not present.
     */
    private DoubleRange constraint;

    /*
     * 
     */
    public DoubleRangeEditor()
    {
        super(EDITOR_PRECISION_DIGITS);
    }

    /*
     * 
     */
    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);

        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof DoubleRange)
            {
                constraint = (DoubleRange) ann;
                break;
            }
        }

        final double min;
        final double max;
        final double increment;
        final double pageIncrement;

        if (constraint != null)
        {
            min = constraint.min();
            max = constraint.max();
            increment = RangeUtils.getDoubleMinorTicks(min, max);
            pageIncrement = RangeUtils.getDoubleMajorTicks(min, max);
        }
        else
        {
            min = Double.MIN_VALUE;
            max = Double.MAX_VALUE;
            increment = 1;
            pageIncrement = 10;
        }

        setRanges(to_i(min), to_i(max), 
            to_i(increment), to_i(pageIncrement));
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
