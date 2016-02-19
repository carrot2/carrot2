
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
