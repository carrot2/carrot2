package org.carrot2.workbench.editors;

import org.eclipse.swt.widgets.Composite;

class EmptyAttributeEditorAdapter extends AttributeEditorAdapter
{
    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
    }

    @Override
    public Object getValue()
    {
        return null;
    }

    @Override
    public void setValue(Object object)
    {
    }
}
