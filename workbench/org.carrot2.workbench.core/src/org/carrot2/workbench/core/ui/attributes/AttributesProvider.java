package org.carrot2.workbench.core.ui.attributes;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;

public abstract class AttributesProvider
{
    private List<AttributeChangeListener> listeners =
        new ArrayList<AttributeChangeListener>();

    public abstract BindableDescriptor createBindableDescriptor();

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.add(listener);
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.remove(listener);
    }

    public abstract void setAttributeValue(String key, Object value);

    protected void fireAttributeChanged(AttributeChangeEvent event)
    {
        for (AttributeChangeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }
}
