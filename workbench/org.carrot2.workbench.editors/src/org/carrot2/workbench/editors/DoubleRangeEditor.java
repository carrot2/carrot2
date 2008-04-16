package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;

public class DoubleRangeEditor extends RangeEditorBase
{
    private DoubleRange constraint;

    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof DoubleRange)
            {
                constraint = (DoubleRange) ann;
            }
        }
    }

    @Override
    public void setValue(Object currentValue)
    {
        setIntValue(convertToInt((Double) currentValue));
    }

    @Override
    public Object getValue()
    {
        return convertToDouble(getIntValue());
    }

    @Override
    protected int getIncrement()
    {
        if (isBounded())
        {
            return convertToInt(RangeUtils.getDoubleMinorTicks(constraint.min(),
                constraint.max()));
        }
        else
        {
            return convertToInt(1);
        }
    }

    @Override
    protected int getMaximum()
    {
        return convertToInt(constraint.max());
    }

    @Override
    protected int getMinimum()
    {
        return convertToInt(constraint.min());
    }

    @Override
    protected int getPageIncrement()
    {
        if (isBounded())
        {
            return convertToInt(RangeUtils.getDoubleMajorTicks(constraint.min(),
                constraint.max()));
        }
        else
        {
            return convertToInt(10);
        }
    }

    private int convertToInt(double doubleValue)
    {
        return Math.round((float) ((Math.pow(10, getDigits()) * doubleValue)));
    }

    private double convertToDouble(int intValue)
    {
        return (intValue / Math.pow(10, getDigits()));
    }

    @Override
    protected boolean isBounded()
    {
        return (constraint.max() + 1 < Integer.MAX_VALUE);
    }

    @Override
    protected int getDigits()
    {
        return 2;
    }
}
