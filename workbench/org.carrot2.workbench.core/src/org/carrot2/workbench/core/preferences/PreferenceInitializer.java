package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        final IPreferenceStore store = WorkbenchCorePlugin.getDefault()
            .getPreferenceStore();

        store.setDefault(PreferenceConstants.P_SHOW_ATTRIBUTES, true);
        store.setDefault(PreferenceConstants.P_SHOW_CLUSTERS, true);
        store.setDefault(PreferenceConstants.P_SHOW_DOCUMENTS, true);
    }
}
