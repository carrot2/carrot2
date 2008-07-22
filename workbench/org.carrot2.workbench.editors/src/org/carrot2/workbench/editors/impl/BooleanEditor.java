package org.carrot2.workbench.editors.impl;

import org.carrot2.workbench.core.helpers.GUIFactory;
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
    public BooleanEditor()
    {
        super(new AttributeEditorInfo(1, true));
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        button = new Button(parent, SWT.CHECK);

        button.setText(descriptor.metadata.getLabelOrTitle());
        button.setToolTipText(descriptor.metadata.getDescription());

        final GridData gridData = GUIFactory.editorGridData()
            .align(SWT.BEGINNING, SWT.BEGINNING)
            .span(gridColumns, 1)
            .create();
        button.setLayoutData(gridData);

        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                fireAttributeChange(new AttributeChangedEvent(BooleanEditor.this));
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
        fireAttributeChange(new AttributeChangedEvent(this));
    }

    @Override
    public Object getValue()
    {
        return button.getSelection();
    }
}
