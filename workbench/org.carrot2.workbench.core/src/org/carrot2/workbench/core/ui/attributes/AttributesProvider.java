package org.carrot2.workbench.core.ui.attributes;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public abstract class AttributesProvider
{
    private List<AttributeChangeListener> listeners =
        new ArrayList<AttributeChangeListener>();
    private List<IPropertyChangeListener> propListeners =
        new ArrayList<IPropertyChangeListener>();

    public abstract BindableDescriptor createBindableDescriptor();

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.add(listener);
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        listeners.remove(listener);
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener)
    {
        propListeners.add(listener);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
        propListeners.remove(listener);
    }

    public abstract void setAttributeValue(String key, Object value);

    public abstract void setPropertyValue(String key, Object value);

    protected void fireAttributeChanged(AttributeChangeEvent event)
    {
        for (AttributeChangeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }

    protected void filePropertyChanged(String propId, Object oldValue, Object newValue)
    {
        for (IPropertyChangeListener propListener : propListeners)
        {
            propListener.propertyChange(new PropertyChangeEvent(this, propId, oldValue,
                newValue));
        }
    }
}
