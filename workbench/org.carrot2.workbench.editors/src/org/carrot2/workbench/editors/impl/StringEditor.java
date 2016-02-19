
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

package org.carrot2.workbench.editors.impl;

import static org.eclipse.swt.SWT.BORDER;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for textual content. Sends
 * {@link IAttributeListener#valueChanged(AttributeEvent)} events only on actual
 * content change, committed by return key traversal or focus lost event. On-keystroke
 * content change is propagated via
 * {@link IAttributeListener#valueChanging(AttributeEvent)}.
 */
public class StringEditor extends AttributeEditorAdapter
{
    /*
     * 
     */
    private Text textBox;

    /*
     * 
     */
    private String content;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        textBox = createTextBox(parent, gridColumns);

        /*
         * React to focus lost.
         */
        textBox.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                checkContentChange();
            }
        });

        textBox.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                fireContentChanging(new AttributeEvent(StringEditor.this, getAttributeKey(), textBox
                    .getText()));
            }
        });

        textBox.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    checkContentChange();
                }
            }
        });

        this.content = textBox.getText();
    }

    protected Text createTextBox(Composite parent, int gridColumns)
    {
        Text textBox = new Text(parent, BORDER);
        textBox.setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .hint(200, SWT.DEFAULT)
                .align(SWT.FILL, SWT.CENTER)
                .span(gridColumns, 1).create());
        return textBox;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        this.textBox.setFocus();
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return content;
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (ObjectUtils.equals(newValue, getValue()))
        {
            return;
        }

        textBox.setText(newValue == null ? "" : newValue.toString());
        checkContentChange();
    }

    /**
     * Check if the content has changed compared to the current value of this attribute.
     * If so, fire an event.
     */
    private void checkContentChange()
    {
        final String textBoxValue = this.textBox.getText();
        if (!ObjectUtils.equals(textBoxValue, content) && isValid(textBoxValue))
        {
            this.content = textBoxValue;
            fireAttributeChanged(new AttributeEvent(this));
        }
    }

    protected boolean isValid(String newValue)
    {
        return true;
    }
}
