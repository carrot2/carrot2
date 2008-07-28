package org.carrot2.workbench.core.preferences;

import org.carrot2.workbench.core.ui.SearchEditorSections;
import org.carrot2.workbench.core.ui.SearchInputView;

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
     * Attribute grouping layout, attributes view.
     */
    public static final String GROUPING_ATTRIBUTE_VIEW = "attributes-view.layout";

    /**
     * Attribute grouping layout, input view.
     */
    public static final String GROUPING_INPUT_VIEW = "search-input-view.layout"; 

    /**
     * Show only required attributes in the {@link SearchInputView}.
     */
    public static final String SHOW_OPTIONAL = "search-input-view.show-required";

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
