package org.carrot2.workbench.core.ui.actions;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * A value switch action bound to {@link IPreferenceStore}.
 */
class ValueSwitchAction extends Action implements IPropertyChangeListener
{
    /**
     * The value of this action in the preference store.
     */
    public final String value;

    /**
     * The key of this action in the preference store.
     */
    public final String key;

    /*
     * 
     */
    public ValueSwitchAction(String key, String value, String label, int style)
    {
        super(label, style);

        this.key = key;
        this.value = value;

        WorkbenchCorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(
            this);
        updateState();
    }

    /*
     * 
     */
    @Override
    public void run()
    {
        WorkbenchCorePlugin.getDefault().getPreferenceStore().setValue(key, value);
    }

    /*
     * 
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        if (ObjectUtils.equals(key, event.getProperty()))
        {
            updateState();
        }
    }

    /*
     * 
     */
    private void updateState()
    {
        final String current = WorkbenchCorePlugin.getDefault().getPreferenceStore().getString(key);
        setChecked(StringUtils.equals(current, value));
    }

    /*
     * Not too pretty, but should work (no explicit dispose).
     */
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();

        WorkbenchCorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(
            this);
    }
}