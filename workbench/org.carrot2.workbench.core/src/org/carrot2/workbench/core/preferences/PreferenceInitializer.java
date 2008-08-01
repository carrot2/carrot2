package org.carrot2.workbench.core.preferences;

import java.util.EnumSet;

import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.ui.SearchEditorSections;
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

        /*
         * Default editor panel properties.
         */
        for (SearchEditorSections s : EnumSet.allOf(SearchEditorSections.class))
        {
            store.setDefault(
                PreferenceConstants.getSectionVisibilityKey(s), true);
        }

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
        store.setDefault(PreferenceConstants.SHOW_OPTIONAL, false);
        store.setDefault(PreferenceConstants.GROUPING_INPUT_VIEW, GroupingMethod.NONE.name());
        
        /*
         * SearchEditor
         */
        store.setDefault(PreferenceConstants.GROUPING_EDITOR_PANEL, GroupingMethod.GROUP.name());

        /*
         * Automatically show attribute info in the view.
         */
        store.setDefault(PreferenceConstants.ATTRIBUTE_INFO_SYNC, false);
    }
}
