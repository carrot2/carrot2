
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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Tuning perspective provides views and layout suitable for tuning clustering algorithms.
 */
public final class TuningPerspective implements IPerspectiveFactory
{
    public static final String ID = "org.carrot2.workbench.core.perspective.tuning";

    /*
     * 
     */
    public void createInitialLayout(IPageLayout layout)
    {
        SearchPerspective.createCommonLayout(layout);
        layout.setEditorAreaVisible(true);
    }
}
