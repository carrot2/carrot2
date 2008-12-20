
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
