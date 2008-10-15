package org.carrot2.workbench.vis.aduna;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/*
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        final Preferences pluginPreferences = AdunaActivator.plugin
            .getPluginPreferences();

        pluginPreferences.setDefault(PreferenceConstants.VISUALIZATION_MODE,
            VisualizationMode.SHOW_FIRST_LEVEL_CLUSTERS.name());
    }
}
