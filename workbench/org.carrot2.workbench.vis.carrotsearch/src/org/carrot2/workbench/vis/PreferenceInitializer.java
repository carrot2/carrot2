
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

package org.carrot2.workbench.vis;

import org.carrot2.workbench.vis.foamtree.LayoutInitializerAction;
import org.carrot2.workbench.vis.foamtree.ToggleRelaxationAction;
import org.carrot2.workbench.vis.foamtree.LayoutInitializerAction.LayoutAlgorithm;
import org.eclipse.core.runtime.preferences.*;

/*
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(Activator.ID);
        prefs.put(LayoutInitializerAction.LAYOUT_INITIALIZER_KEY, LayoutAlgorithm.FISHEYE.name());
        prefs.putBoolean(ToggleRelaxationAction.RELAXATION_ENABLED_KEY, true);
    }
}
