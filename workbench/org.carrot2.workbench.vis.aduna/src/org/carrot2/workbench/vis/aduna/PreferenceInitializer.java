
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
