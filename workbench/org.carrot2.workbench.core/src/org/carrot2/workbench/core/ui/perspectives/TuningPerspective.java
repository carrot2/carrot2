package org.carrot2.workbench.core.ui.perspectives;

import org.carrot2.workbench.core.ui.*;
import org.eclipse.ui.*;

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
        layout.setEditorAreaVisible(true);

        // Display LogView for debugging.
        IFolderLayout bottomFolder = layout.createFolder("bottomViews",
            IPageLayout.BOTTOM, .8f, layout.getEditorArea());
        bottomFolder.addView("org.eclipse.pde.runtime.LogView");

        IFolderLayout leftFolder = layout.createFolder("leftViews", IPageLayout.LEFT,
            .3f, layout.getEditorArea());
        leftFolder.addView(SearchInputView.ID);

        IFolderLayout leftBottomFolder = layout.createFolder("leftBottomViews",
            IPageLayout.BOTTOM, .3f, "leftViews");
        leftBottomFolder.addView(ClusterTreeView.ID);
        leftBottomFolder.addView(AttributeView.ID);

        layout.addShowViewShortcut(ClusterTreeView.ID);
        layout.addShowViewShortcut(AttributeView.ID);
        layout.addShowViewShortcut(SearchInputView.ID);
    }
}
