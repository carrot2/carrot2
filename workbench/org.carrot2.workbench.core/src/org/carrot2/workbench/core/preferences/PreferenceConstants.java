package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.ui.SearchEditorSections;

/**
 * Constant definitions for plug-in preferences
 */
public final class PreferenceConstants
{
    /**
     * Automatically re-render editors after attributes change.
     */
    public static final String AUTO_UPDATE = "auto-update.enabled"; 

    /**
     * {@link #AUTO_UPDATE} delay. 
     */
    public static final String AUTO_UPDATE_DELAY = "auto-update.delay";

    /**
     * Attribute grouping layout.
     */
    public static final String ATTRIBUTE_GROUPING_LAYOUT = "attributes-view.layout"; 

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
