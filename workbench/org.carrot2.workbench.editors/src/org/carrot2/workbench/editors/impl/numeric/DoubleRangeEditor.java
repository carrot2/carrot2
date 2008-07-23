package org.carrot2.workbench.editors.impl.numeric;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.workbench.editors.AttributeEditorInfo;

/**
 * Attribute editor for non-negative, limited-range double values.
 */
final class DoubleRangeEditor extends NumericRangeEditorBase
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
        super(EDITOR_PRECISION_DIGITS);
    }

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        final AttributeEditorInfo info = super.init(descriptor);

        constraint = NumberUtils.getDoubleRange(descriptor);

        final double min = constraint.min();
        final double max = constraint.max();
        final double increment = RangeUtils.getDoubleMinorTicks(min, max);
        final double pageIncrement = RangeUtils.getDoubleMajorTicks(min, max);

        setRanges(to_i(min), to_i(max), to_i(increment), to_i(pageIncrement));

        return info;
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
