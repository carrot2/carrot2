package org.carrot2.workbench.editors;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.widgets.Composite;

public abstract class AttributeEditorAdapter implements IAttributeEditor
{
    protected AttributeDescriptor descriptor;
    private List<AttributeChangeListener> listeners =
        new ArrayList<AttributeChangeListener>();

    public void createEditor(Composite parent)
    {
    }

    public Object getValue()
    {
        return null;
    }

    public void init(AttributeDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getAttributeKey()
    {
        return this.descriptor.key;
    }

    public void dispose()
    {
    }

    public void setValue(Object currentValue)
    {
    }

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.add(listener);
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireAttributeChange(AttributeChangeEvent event)
    {
        for (AttributeChangeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }
}
