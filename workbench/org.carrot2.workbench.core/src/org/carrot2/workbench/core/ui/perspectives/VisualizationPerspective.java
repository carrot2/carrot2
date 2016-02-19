
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

package org.carrot2.workbench.core.ui.perspectives;

import org.eclipse.ui.*;

/**
 * Visualization perspective provides views and layout suitable for visually browsing
 * clusters.
 */
public final class VisualizationPerspective implements IPerspectiveFactory
{
    public static final String ID = "org.carrot2.workbench.core.perspective.visualization";

    /*
     * 
     */
    public void createInitialLayout(IPageLayout layout)
    {
        // layout.addView("org.carrot2.workbench.views.aduna", IPageLayout.RIGHT, 0.3f, layout.getEditorArea());
        layout.setEditorAreaVisible(true);
    }
}
