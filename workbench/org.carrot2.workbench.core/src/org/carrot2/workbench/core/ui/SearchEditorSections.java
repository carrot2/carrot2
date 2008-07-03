package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.preferences.PreferenceConstants;

/**
 * {@link SearchEditor} has several panels. These panels are identifier with constants in
 * this enum. Their visual attributes and preference keys are also configured here.
 * <p>
 * These panels are <b>required</b> by {@link SearchEditor} and you should not remove any
 * of these constants.
 */
public enum SearchEditorSections
{
    CLUSTERS("Clusters", 1, ClusterTreeView.ID, PreferenceConstants.P_SHOW_CLUSTERS),
    DOCUMENTS("Documents", 2, DocumentListView.ID, PreferenceConstants.P_SHOW_DOCUMENTS),
    ATTRIBUTES("Attributes", 2, AttributesView.ID, PreferenceConstants.P_SHOW_ATTRIBUTES);

    /** Default weight. */
    public final int weight;

    /** Default name. */
    public final String name;

    /** Icon identifier. */
    public final String iconID;

    /** @see PreferenceConstants */
    final String defaultVisibility;

    private SearchEditorSections(String name, int weight, String iconID,
        String defaultVisibility)
    {
        this.weight = weight;
        this.name = name;
        this.iconID = iconID;
        this.defaultVisibility = defaultVisibility;
    }
}