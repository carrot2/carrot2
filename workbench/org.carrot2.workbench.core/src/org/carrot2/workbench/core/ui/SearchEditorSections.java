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
    CLUSTERS("Clusters", 1, ClusterTreeView.ID),
    DOCUMENTS("Documents", 2, DocumentListView.ID),
    ATTRIBUTES("Attributes", 2, AttributesView.ID);

    /** Default weight. */
    public final int weight;

    /** Default name. */
    public final String name;

    /** Icon identifier. */
    public final String iconID;

    private SearchEditorSections(String name, int weight, String iconID)
    {
        this.weight = weight;
        this.name = name;
        this.iconID = iconID;
    }
}