package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * Attribute editor for integer values, possibly with {@link IntRange} annotations.
 */
public final class IntegerRangeEditor extends NumericRangeEditorBase
{
    /**
     * Range constraint or <code>null</code> if not present.
     */
    private IntRange constraint;

    /*
     * 
     */
    public IntegerRangeEditor()
    {
        super(/* precision digits */ 0);
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
            if (ann instanceof IntRange)
            {
                constraint = (IntRange) ann;
                break;
            }
        }

        final int min;
        final int max;
        final int increment;
        final int pageIncrement;

        if (constraint != null)
        {
            min = constraint.min();
            max = constraint.max();
            increment = RangeUtils.getIntMinorTicks(min, max);
            pageIncrement = RangeUtils.getIntMajorTicks(min, max);
        }
        else
        {
            min = Integer.MIN_VALUE;
            max = Integer.MAX_VALUE;
            increment = 1;
            pageIncrement = 10;
        }

        setRanges(min, max, increment, pageIncrement);
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

        super.propagateNewValue(((Number) value).intValue());
    }
}
