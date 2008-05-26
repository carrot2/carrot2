package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.SearchView;
import org.carrot2.workbench.core.ui.views.ClusterTreeView;
import org.carrot2.workbench.core.ui.views.DocumentListView;
import org.eclipse.ui.*;

public class Perspective implements IPerspectiveFactory
{
    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);

        // Display LogView for debugging.
        IFolderLayout bottomFolder =
            layout.createFolder("bottomViews", IPageLayout.BOTTOM, .8f, layout
                .getEditorArea());
        bottomFolder.addView("org.eclipse.pde.runtime.LogView");
        bottomFolder.addView(DocumentListView.ID);

        IFolderLayout leftFolder =
            layout.createFolder("leftViews", IPageLayout.LEFT, .3f, layout
                .getEditorArea());
        leftFolder.addView(SearchView.ID);

        IFolderLayout leftBottomFolder =
            layout.createFolder("leftBottomViews", IPageLayout.BOTTOM, .3f, "leftViews");
        leftBottomFolder.addView(ClusterTreeView.ID);

        layout.addShowViewShortcut(ClusterTreeView.ID);
        layout.addShowViewShortcut(DocumentListView.ID);
        layout.addShowViewShortcut(SearchView.ID);
    }
}
