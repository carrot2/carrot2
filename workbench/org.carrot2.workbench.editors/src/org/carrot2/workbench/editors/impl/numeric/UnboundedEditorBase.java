
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

import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

/**
 * Base class for unbounded editors.
 */
abstract class UnboundedEditorBase<T extends Number> extends AttributeEditorAdapter
{
    /**
     * Text box for editing.
     */
    protected Text text;

    /** Last valid value cache. */
    private T lastValidValue;

    /**
     * A temporary flag used to avoid event looping.
     */
    private boolean duringSelection;

    protected String tooltip;
    protected T min;
    protected T max;
    protected T pageIncrement;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        return new AttributeEditorInfo(1, false);
    }

    /*
     * Return the current editor value.
     */
    @Override
    public Object getValue()
    {
        return lastValidValue;
    }

    /*
     * 
     */
    @Override
    public void setValue(Object object)
    {
        if (object != null && object.equals(getValue()))
        {
            return;
        }

        if (object != null && !(object instanceof Number))
        {
            return;
        }

        if (object == null)
        {
            propagateNewValue(null);
        }
        else
        {
            propagateNewValue(to_s((Number) object));
        }
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        createText(parent, gridColumns);
    }

    /**
     * Create the scale control.
     */
    private void createText(Composite parent, int gridColumns)
    {
        final GridDataFactory factory = GUIFactory.editorGridData();

        final GridData layoutData = factory.create();
        layoutData.horizontalSpan = gridColumns;
        layoutData.grabExcessHorizontalSpace = false;
        layoutData.verticalAlignment = SWT.CENTER;

        text = new Text(parent, SWT.LEAD | SWT.SINGLE | SWT.BORDER);
        text.setLayoutData(layoutData);

        /*
         * Hook event listener.
         */
        text.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                /*
                 * Propagate valid intermediate values as soon
                 * as the user types them in, but do not
                 * refresh {@link #text}, as it causes selection caret to be reset. 
                 */
                if (isValid(text.getText()))
                {
                    propagateNewValue(text.getText(), false);
                }
            }
        });

        text.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                /*
                 * Update with last valid value on focus lost (if the intermediate
                 * value was for some reason invalid, for example consisted
                 * only of a '-' sign, it will be reverted to the last valid value.
                 */
                propagateNewValue(to_s((Number) lastValidValue));
            }
        });

        text.addVerifyListener(new VerifyListener()
        {
            /*
             * Allow only these modifications that result in a valid value or
             * a temporary string that can lead to one.
             */
            public void verifyText(VerifyEvent e)
            {
                if (duringSelection) return;

                final String currentText = text.getText();
                final String newText = currentText.substring(0, e.start) + e.text
                    + currentText.substring(e.end);

                e.doit = StringUtils.isEmpty(newText) || isValidForEditing(newText);
            }
        });

        text.addListener(SWT.MouseWheel, new Listener()
        {
            /*
             * On mouse wheel, update value by page increment and
             * stop wheel event's propagation.
             */
            public void handleEvent(Event event)
            {
                event.doit = false;

                if (getValue() != null)
                {
                    doPageIncrement(event.count > 0);
                }
            }
        });

        text.setToolTipText(tooltip);
    }

    /**
     * {@Link #propagateNewValue(String, boolean)} and refresh {@link #text}'s value.
     */
    protected final void propagateNewValue(String value)
    {
        propagateNewValue(value, true);
    }
    
    /**
     * Propagates value change event to all listeners and updates GUI widgets.
     */
    protected final void propagateNewValue(String value, boolean refreshTextBox)
    {
        if (!this.duringSelection)
        {
            this.duringSelection = true;

            if (refreshTextBox)
            {
                this.text.setText(value == null ? "" : value);
            }

            if (isValid(value))
            {
                try
                {
                    this.lastValidValue = toRange(to_v(value));
                    fireAttributeChanged(new AttributeEvent(this));
                }
                catch (NumberFormatException e)
                {
                    // Just skip.
                }
            }

            this.duringSelection = false;
        }
    }

    /*
     * 
     */
    protected abstract void doPageIncrement(boolean positive);

    /*
     * 
     */
    protected abstract T toRange(T d);

    /**
     * Should return <code>true</code> if and only if the value is valid
     * and will parse with {@link #to_v}.
     */
    protected abstract boolean isValid(String value);

    /**
     * Should return <code>true</code> if the value is valid as a temporal value of the
     * input text editor, but may not validate with {@link #isValid(String)}. Examples:
     * <code>-</code>, <code>+</code> and an empty string are all valid temporary
     * states, but not numbers.
     */
    protected abstract boolean isValidForEditing(String value);

    /**
     * Parse the number and return its string representation.
     */
    protected abstract String to_s(Number object);

    /*
     * 
     */
    protected abstract T to_v(String value);
}
