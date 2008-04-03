package org.carrot2.workbench.editors;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.widgets.Composite;

public abstract class AttributeEditorAdapter implements IAttributeEditor
{
    public void createEditor(Composite parent)
    {
    }

    public Object getValue()
    {
        return null;
    }

    public void init(AttributeDescriptor descriptor)
    {
    }

    public void dispose()
    {
    }

    public void setValue(Object currentValue)
    {
    }
}
