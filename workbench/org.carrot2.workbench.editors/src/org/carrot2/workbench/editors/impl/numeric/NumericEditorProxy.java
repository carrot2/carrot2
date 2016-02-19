
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

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.constraint.DoubleRange;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.widgets.*;

/**
 * This is a common editor for all kinds of numeric types, with or without constraints.
 * Depending on the availability of constraints, it proxies contract calls to one of more
 * specific editors.
 * <p>
 * This proxy cannot be easily implemented using extension point's constraint matching
 * criteria, because there are dependencies on the values of constraints (ranges). For
 * example, SWT's {@link Scale} and {@link Slider} controls do not allow negative values
 * and need to be emulated.
 */
public final class NumericEditorProxy implements IAttributeEditor
{
    /**
     * Actual delegate to which we will forward calls.
     */
    private IAttributeEditor delegate;

    /**
     * Initialize and instantiate the delegate editor class.
     */
    public AttributeEditorInfo init(BindableDescriptor bindable,
        AttributeDescriptor descriptor, IAttributeEventProvider eventProvider,
        Map<String, Object> defaultValues)
    {
        final boolean floatingPointType = descriptor.type.equals(Double.class)
            || descriptor.type.equals(Float.class);

        final boolean unbounded;
        final boolean hasNegativeValues;
        if (floatingPointType)
        {
            final DoubleRange r = NumberUtils.getDoubleRange(descriptor);

            hasNegativeValues = (r == null || r.min() < 0);
            unbounded = (r == null || NumberUtils.isUnbounded(r));
        }
        else
        {
            final IntRange r = NumberUtils.getIntRange(descriptor);

            hasNegativeValues = (r == null || r.min() < 0);
            unbounded = (r == null || NumberUtils.isUnbounded(r));
        }

        final IAttributeEditor delegate;
        if (unbounded)
        {
            /*
             * If the range is unbounded or contains negative values, use unbounded
             * numeric editor (simple text box). Such unconstrained public attributes
             * shouldn't be common anyway.
             */
            if (floatingPointType)
            {
                delegate = new UnboundedDoubleEditor();
            }
            else
            {
                if (!hasNegativeValues)
                {
                    delegate = new IntegerRangeEditor();
                }
                else
                {
                    delegate = new UnboundedIntegerEditor();
                }
            }
        }
        else if (hasNegativeValues)
        {
            /*
             * If the range covers negative values, or if the range is too big to fit in
             * the integer range, use unbounded double editor. TODO: Negative
             * scale/sliders are available in Eclipse 3.4M5:
             * https://bugs.eclipse.org/bugs/show_bug.cgi?id=91317
             */
            if (floatingPointType)
            {
                delegate = new UnboundedDoubleEditor();
            }
            else
            {
                delegate = new UnboundedIntegerEditor();
            }
        }
        else
        {
            /*
             * The range exists, is bounded and contains only non-negative values, use
             * slider/scale directly.
             */
            if (floatingPointType)
            {
                delegate = new DoubleRangeEditor();
            }
            else
            {
                delegate = new IntegerRangeEditor();
            }
        }

        this.delegate = delegate;
        return delegate.init(bindable, descriptor, eventProvider, defaultValues);
    }

    public void createEditor(Composite parent, int gridColumns)
    {
        delegate.createEditor(parent, gridColumns);
    }

    public void setFocus()
    {
        delegate.setFocus();
    }

    public void removeAttributeListener(IAttributeListener listener)
    {
        delegate.removeAttributeListener(listener);
    }

    public void addAttributeListener(IAttributeListener listener)
    {
        delegate.addAttributeListener(listener);
    }

    public void setValue(Object currentValue)
    {
        delegate.setValue(currentValue);
    }

    public Object getValue()
    {
        return delegate.getValue();
    }

    public String getAttributeKey()
    {
        return delegate.getAttributeKey();
    }

    public void dispose()
    {
        delegate.dispose();
    }
}
