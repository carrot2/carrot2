
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

import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * Common class for both floating point (with arbitrary precision of decimal digits) and
 * integer, non-negative numeric ranges.
 * <p>
 * The widget is composed of a {@link Scale} and a {@link Spinner}, both of which can be
 * used to modify the current value of the attribute.
 */
abstract class NumericRangeEditorBase extends AttributeEditorAdapter
{
    /** */
    private Scale scale;

    /** */
    private Spinner spinner;

    /**
     * A temporary flag used to avoid event looping.
     */
    private boolean duringSelection;

    /*
     * Numeric ranges.
     */

    private int min;
    private int max;
    private int precisionDigits;

    private int increment;
    private int pageIncrement;

    private boolean minBounded;
    private boolean maxBounded;

    /**
     * Value multiplier needed to convert between fixed precision floating point values
     * and integers.
     */
    private final double multiplier;

    /** Tooltip with allowed range. */
    private String tooltip;

    private int maxSpinnerWidth;

    /**
     * A copy of the current value of this editor. Added because it turns out 
     * spinner widgets emit delayed events (and cause extra events to be fired because
     * they don't update their state immediately).
     */
    private int currentValue;

    /**
     * @param precisionDigits Number of digits after decimal separator.
     */
    public NumericRangeEditorBase(int precisionDigits)
    {
        this.precisionDigits = precisionDigits;
        this.multiplier = Math.pow(10, precisionDigits);
    }
    
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        return new AttributeEditorInfo(2, false);
    }

    /**
     * Initialize numeric ranges, according to the descriptor's definition.
     */
    protected final void setRanges(int min, int max, int increment, int pageIncrement)
    {
        this.min = min;
        this.minBounded = (min != Integer.MIN_VALUE);

        this.max = max;
        this.maxBounded = (max != Integer.MAX_VALUE);

        this.increment = increment;
        this.pageIncrement = pageIncrement;

        if (!minBounded && !maxBounded)
        {
            this.tooltip = "Valid range: unbounded";
        }
        else
        {
            this.tooltip = "Valid range: [" + to_s(min) + "; " + to_s(max) + "]";
        }
    }

    /*
     * Return the current editor value.
     */
    @Override
    public Object getValue()
    {
        return currentValue;
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        final GridDataFactory factory = GUIFactory.editorGridData();

        final GridData spinnerLayoutData = factory.create();
        spinnerLayoutData.horizontalSpan = 1;
        spinnerLayoutData.grabExcessHorizontalSpace = false;
        spinnerLayoutData.verticalAlignment = SWT.CENTER;

        final GridData scaleLayoutData = factory.span(gridColumns - 1, 1).grab(true,
            false).create();

        if (minBounded && maxBounded)
        {
            createScale(parent);
            scale.setLayoutData(scaleLayoutData);
        }
        else
        {
            spinnerLayoutData.horizontalSpan = 2;
            spinnerLayoutData.grabExcessHorizontalSpace = true;
        }

        createSpinner(parent);

        spinner.setSelection(spinner.getMaximum());
        spinnerLayoutData.widthHint = maxSpinnerWidth;
        spinnerLayoutData.horizontalAlignment = SWT.FILL;

        spinner.setLayoutData(spinnerLayoutData);
    }

    /**
     * Create the scale control.
     */
    private void createScale(Composite holder)
    {
        scale = new Scale(holder, SWT.HORIZONTAL);
        
        scale.setMaximum(max);
        scale.setMinimum(min);
        
        scale.setIncrement(increment);
        scale.setPageIncrement(pageIncrement);
        scale.setToolTipText(tooltip);

        /*
         * Hook event listener to the scale component.
         */
        scale.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (scale.getSelection() != currentValue)
                    propagateNewValue(scale.getSelection());
            }
        });

        scale.addMouseWheelListener(new MouseWheelListener()
        {
            public void mouseScrolled(MouseEvent e)
            {
                if (scale.getSelection() != currentValue)
                    propagateNewValue(scale.getSelection());
            }
        });
    }

    /**
     * Create the spinner control.
     */
    private void createSpinner(Composite holder)
    {
        spinner = new Spinner(holder, SWT.BORDER);

        /*
         * Calculate maximum spinner width (consistently among all editors).
         */
        spinner.setMaximum(1000);
        spinner.setMinimum(0);
        spinner.setDigits(2);

        this.maxSpinnerWidth = spinner.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

        /*
         * Now proceed with setting the actual values.
         */
        
        spinner.setMaximum(max);
        spinner.setMinimum(min);
        spinner.setSelection(min);
        spinner.setDigits(precisionDigits);
        spinner.setToolTipText(tooltip);

        if (minBounded && maxBounded)
        {
            spinner.setIncrement(increment);
            spinner.setPageIncrement(pageIncrement);
        }

        spinner.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (currentValue != spinner.getSelection())
                    propagateNewValue(spinner.getSelection());
            }
        });
    }

    /**
     * Propagates value change event to all listeners and updates
     * GUI widgets.
     */
    protected final void propagateNewValue(int value)
    {
        if (!this.duringSelection)
        {
            this.duringSelection = true;
            this.currentValue = value;

            if (spinner != null && spinner.getSelection() != value)
            {
                spinner.setSelection(value);
            }

            if (scale != null && scale.getSelection() != value)
            {
                scale.setSelection(value);
            }

            this.duringSelection = false;

            fireAttributeChanged(new AttributeEvent(this));
        }
    }

    /**
     * Convert between double values and integer values (taking into account precision
     * shift).
     */
    protected final int to_i(double v)
    {
        if (v == Double.MIN_VALUE)
        {
            return Integer.MIN_VALUE;
        }

        if (v == Double.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }

        if (Double.isNaN(v))
        {
            return 0;
        }

        return (int) Math.round(v * multiplier);
    }

    /**
     * Convert between double values and integer values (taking into account precision
     * shift).
     */
    protected final double to_d(int i)
    {
        return i / multiplier;
    }

    /*
     * Converts the given argument to a human-readable string.
     */
    private String to_s(int v)
    {
        if (v == Integer.MIN_VALUE)
        {
            return "-\u221E";
        }
        else if (v == Integer.MAX_VALUE)
        {
            return "\u221E";
        }
        else
        {
            return String.format("%." + precisionDigits + "f", to_d(v));
        }
    }
}
