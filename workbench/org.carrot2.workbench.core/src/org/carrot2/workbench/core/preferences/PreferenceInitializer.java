
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

package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.ui.SearchEditor.PanelName;

import java.util.EnumMap;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditor.PanelState;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        final IPreferenceStore store = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        /*
         * Default editor panels.
         */
        final EnumMap<SearchEditor.PanelName, SearchEditor.PanelState> globals = 
            Maps.newEnumMap(SearchEditor.PanelName.class);
        globals.put(PanelName.CLUSTERS, createPanelState(1, true));
        globals.put(PanelName.DOCUMENTS, createPanelState(2, true));
        globals.put(PanelName.ATTRIBUTES, createPanelState(1, false));
        SearchEditor.saveGlobalPanelsState(globals);

        /*
         * Auto-update.
         */
        store.setDefault(PreferenceConstants.AUTO_UPDATE, true);
        store.setDefault(PreferenceConstants.AUTO_UPDATE_DELAY, 1000);
        
        /*
         * Attribute grouping.
         */
        store.setDefault(PreferenceConstants.GROUPING_ATTRIBUTE_VIEW, GroupingMethod.GROUP.name());

        /*
         * SearchInputView
         */
        store.setDefault(PreferenceConstants.SHOW_OPTIONAL, true);
        store.setDefault(PreferenceConstants.GROUPING_INPUT_VIEW, GroupingMethod.LEVEL.name());
        
        /*
         * SearchEditor
         */
        store.setDefault(PreferenceConstants.GROUPING_EDITOR_PANEL, GroupingMethod.GROUP.name());

        /*
         * Automatically show attribute info in the view.
         */
        store.setDefault(PreferenceConstants.ATTRIBUTE_INFO_SYNC, true);
        
        /*
         * Truncate long snippets and titles after this many characters.
         */
        store.setDefault(PreferenceConstants.MAX_FIELD_LENGTH, 280);
    }

    /*
     * 
     */
    private static PanelState createPanelState(int weight, boolean visibility)
    {
        final PanelState ps = new PanelState();
        ps.visibility = visibility;
        ps.weight = weight;
        return ps;
    }
}
