
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

package org.carrot2.workbench.vis.aduna;

import org.eclipse.core.runtime.preferences.*;

/*
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(AdunaActivator.PLUGIN_ID);
        prefs.put(PreferenceConstants.VISUALIZATION_MODE, VisualizationMode.SHOW_FIRST_LEVEL_CLUSTERS.name());
    }
}
