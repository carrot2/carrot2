package org.carrot2.workbench.vis.aduna;

/**
 * How the view should react to selection in editors?
 */
enum VisualizationMode
{
    SHOW_ALL_CLUSTERS("Show all clusters"),
    SHOW_FIRST_LEVEL_CLUSTERS("Show first-level clusters"),
    SHOW_SELECTED_CLUSTERS("Show selected clusters only");

    private final String label;
    
    private VisualizationMode(String label)
    {
        this.label = label;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
