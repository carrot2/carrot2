package org.carrot2.workbench.editors.impl.numeric;

import java.util.regex.Pattern;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.workbench.editors.AttributeEditorInfo;

/**
 * An editor for unbounded floating point (double) attributes.
 */
final class UnboundedDoubleEditor extends UnboundedEditorBase<Double>
{
    /**
     * Optional constraint.
     */
    private DoubleRange constraint;
    
    /**
     * Validation pattern for doubles. We allow decimal format only (no scientific format).
     */
    private final static Pattern pattern = Pattern.compile("[\\-\\+]?[0-9]+[.]?[0-9]*");

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        constraint = NumberUtils.getDoubleRange(descriptor);

        if (constraint != null)
        {
            min = constraint.min();
            max = constraint.max();
        }
        else
        {
            min = Double.MIN_VALUE;
            max = Double.MAX_VALUE;
        }

        pageIncrement = 1.0d;
        tooltip = NumberUtils.getTooltip(min, max);

        return super.init(descriptor);
    }

    /**
     * Parse the number and return its string representation.
     */
    @Override
    protected String to_s(Number object)
    {
        return NumberUtils.to_decimal(object.doubleValue());
    }

    /*
     * 
     */
    @Override
    protected void doPageIncrement(boolean positive)
    {
        if (getValue() == null)
        {
            setValue(0);
        }
        else
        {
            setValue(toRange((Double) getValue() + (positive ? pageIncrement : -pageIncrement)));
        }
    }

    /*
     * 
     */
    @Override
    protected Double toRange(Double d)
    {
        if (d <= min) d = min;
        if (d >= max) d = max;
        return d;
    }

    /*
     * 
     */
    @Override
    protected boolean isValid(String value)
    {
        if (pattern.matcher(value).matches())
        {
            try
            {
                final double v = to_v(value);
                if (v >= min && v <= max)
                {
                    return true;
                }
            }
            catch (NumberFormatException e)
            {
                // Fall through.
            }
        }

        return false;
    }

    /*
     * 
     */
    @Override
    protected Double to_v(String value)
    {
        return Double.parseDouble(value);
    }
}
