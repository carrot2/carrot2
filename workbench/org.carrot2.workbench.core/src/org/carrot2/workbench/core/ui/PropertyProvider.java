package org.carrot2.workbench.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public abstract class PropertyProvider
{

    private List<IPropertyChangeListener> propListeners =
        new ArrayList<IPropertyChangeListener>();

    public void addPropertyChangeListener(IPropertyChangeListener listener)
    {
        propListeners.add(listener);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
        propListeners.remove(listener);
    }

    protected void firePropertyChanged(String propId, Object oldValue, Object newValue)
    {
        for (IPropertyChangeListener propListener : propListeners)
        {
            propListener.propertyChange(new PropertyChangeEvent(this, propId, oldValue,
                newValue));
        }
    }

    public void dispose()
    {
        propListeners.clear();
    }

}