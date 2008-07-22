package org.carrot2.workbench.editors.impl;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.editors.AttributeEditorInfo;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;

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
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        /*
         * If the range is unbounded, use unbounded numeric editor (simple text box). Such
         * unconstrained public attributes shouldn't be common anyway.
         */

        /*
         * If the range covers negative values, or if the range is too big to fit in the
         * integer range, map the range onto a scale using proportional mapping and
         * emulate a slider with a simple text box (with validation).
         */

        /*
         * The range exists, is bounded and contains only non-negative values, use
         * slider/scale directly.
         */

        return null;
    }

    public void createEditor(Composite parent, int gridColumns)
    {
        delegate.createEditor(parent, gridColumns);
    }

    public void removeAttributeChangeListener(IAttributeListener listener)
    {
        delegate.removeAttributeChangeListener(listener);
    }

    public void addAttributeChangeListener(IAttributeListener listener)
    {
        delegate.addAttributeChangeListener(listener);
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
