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
