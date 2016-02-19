
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * An abstract class that reacts on changes to a given property in the
 * {@link IPreferenceStore}.
 */
public abstract class PropertyChangeListenerAdapter implements IPropertyChangeListener
{
    protected final String property;
    
    public PropertyChangeListenerAdapter(String property)
    {
        this.property = property;
    }
    
    public void propertyChange(PropertyChangeEvent event)
    {
        if (ObjectUtils.equals(property, event.getProperty()))
        {
            propertyChangeFiltered(event);
        }
    }

    protected abstract void propertyChangeFiltered(PropertyChangeEvent event);
}
