
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

import java.util.Map;

import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.ui.AttributeInfoTooltip;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Editor for {@link Boolean} values, rendered as a toggle button.
 */
public final class BooleanEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    /* */
    private Button button;

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        return new AttributeEditorInfo(1, true);
    }
    
    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        button = new Button(parent, SWT.CHECK);
        AttributeInfoTooltip.attach(button, descriptor);

        button.setText(descriptor.metadata.getLabelOrTitle());

        final GridData gridData = GUIFactory.editorGridData()
            .align(SWT.BEGINNING, SWT.BEGINNING)
            .span(gridColumns, 1)
            .grab(true, false)
            .create();
        button.setLayoutData(gridData);

        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                fireAttributeChanged(new AttributeEvent(BooleanEditor.this));
            }
        });
    }

    @Override
    public void setValue(Object newValue)
    {
        if (getValue().equals(newValue))
        {
            return;
        }

        button.setSelection((Boolean) newValue);
        fireAttributeChanged(new AttributeEvent(this));
    }

    @Override
    public Object getValue()
    {
        return button.getSelection();
    }
}
