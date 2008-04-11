package org.carrot2.workbench.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BooleanEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    private Button button;

    @Override
    public void createEditor(Composite parent)
    {
        button = new Button(parent, SWT.CHECK);
        assert (descriptor != null);
        assert (descriptor.metadata != null);
        button.setText(descriptor.metadata.getLabel());
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
}
