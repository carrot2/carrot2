package org.carrot2.workbench.core.ui.actions;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * {@link IPropertyHost} delegating to {@link IPreferenceStore}.
 */
public final class PreferenceStorePropertyHost implements IPropertyHost
{
    private final IPreferenceStore store;

    public PreferenceStorePropertyHost(IPreferenceStore preferenceStore)
    {
        this.store = preferenceStore;
    }

    public void addPropertyChangeListener(IPropertyChangeListener listener)
    {
        store.addPropertyChangeListener(listener);
    }

    public String getProperty(String key)
    {
        return store.getString(key);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
        store.removePropertyChangeListener(listener);
    }

    public void setProperty(String key, String value)
    {
        store.setValue(key, value);
    }
}
