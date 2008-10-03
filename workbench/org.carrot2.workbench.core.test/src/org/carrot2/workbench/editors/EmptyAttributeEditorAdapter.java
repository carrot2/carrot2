package org.carrot2.workbench.editors;

import org.eclipse.swt.widgets.Composite;

class EmptyAttributeEditorAdapter extends AttributeEditorAdapter
{
    @Override
    protected AttributeEditorInfo init()
    {
        return new AttributeEditorInfo(1, false);
    }

    @Override
    public void createEditor(Composite parent, int gridColumns)
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
