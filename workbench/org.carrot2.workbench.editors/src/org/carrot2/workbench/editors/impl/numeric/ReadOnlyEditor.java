
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * An dummy editor for read-only numerics.
 */
final class ReadOnlyEditor extends AttributeEditorAdapter
{
    /** */
    private Text text;
    
    /** */
    private Object lastValue;

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
        return lastValue;
    }

    @Override
    public void setValue(Object object)
    {
        if (lastValue == object)
        {
            return;
        }

        this.lastValue = object;
        this.text.setText(lastValue == null ? "(null)" : lastValue.toString());

        fireAttributeChanged(new AttributeEvent(this));
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

        text = new Text(parent, SWT.RIGHT | SWT.SINGLE);
        text.setEnabled(false);
        text.setLayoutData(layoutData);
    }
}
