package org.carrot2.workbench.editors;

import java.lang.annotation.Annotation;

import org.carrot2.util.RangeUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.IntRange;

public class IntegerRangeEditor extends RangeEditorBase
{
    private IntRange constraint;

    @Override
    public Object getValue()
    {
        return getIntValue();
    }

    @Override
    public void init(AttributeDescriptor descriptor)
    {
        super.init(descriptor);
        for (Annotation ann : descriptor.constraints)
        {
            if (ann instanceof IntRange)
            {
                constraint = (IntRange) ann;
            }
        }
    }

    @Override
    public void setValue(Object currentValue)
    {
        setIntValue((Integer) currentValue);
    }

    @Override
    protected int getDigits()
    {
        return 0;
    }

    @Override
    protected int getIncrement()
    {
        if (isBounded())
        {
            return RangeUtils.getIntMinorTicks(constraint.min(), constraint.max());
        }
        else
        {
            return 1;
        }
    }

    @Override
    protected int getMaximum()
    {
        return constraint.max();
    }

    @Override
    protected int getMinimum()
    {
        return constraint.min();
    }

    @Override
    protected int getPageIncrement()
    {
        if (isBounded())
        {
            return RangeUtils.getIntMajorTicks(constraint.min(), constraint.max());
        }
        else
        {
            return 10;
        }
    }

    @Override
    protected boolean isBounded()
    {
        return (constraint.max() < Integer.MAX_VALUE);
    }

}
