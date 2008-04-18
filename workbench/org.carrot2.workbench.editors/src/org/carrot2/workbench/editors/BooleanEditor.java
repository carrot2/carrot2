package org.carrot2.workbench.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BooleanEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    private Button button;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        button = new Button(parent, SWT.CHECK);
        assert (descriptor != null);
        assert (descriptor.metadata != null);
        button.setText(descriptor.metadata.getLabelOrTitle());
        button.setLayoutData(layoutData);
        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                doEvent();
            }
        });
    }

    private void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        boolean value = (Boolean) currentValue;
        button.setSelection(value);
    }

    @Override
    public Object getValue()
    {
        return button.getSelection();
    }

    @Override
    public boolean containsLabel()
    {
        return true;
    }
}
