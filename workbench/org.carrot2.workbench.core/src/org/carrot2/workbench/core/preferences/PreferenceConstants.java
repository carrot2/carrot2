package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.ui.SearchEditorSections;

/**
 * Constant definitions for plug-in preferences
 */
public final class PreferenceConstants
{
    /*
     * 
     */
    private PreferenceConstants()
    {
        // No instances.
    }

    /**
     * Returns preference key for a given editor section's visibility.
     */
    public static String getSectionVisibilityKey(SearchEditorSections s)
    {
        return s.name + ".visible";
    }

    /**
     * Returns preference key for a given editor section's weight.
     */
    public static String getSectionWeightKey(SearchEditorSections s)
    {
        return s.name + ".weight";
    }
}
