package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.SearchView;
import org.carrot2.workbench.core.ui.views.ClusterTreeView;
import org.carrot2.workbench.core.ui.views.DocumentListView;
import org.eclipse.ui.*;

public class Perspective implements IPerspectiveFactory
{
    public void createInitialLayout(IPageLayout layout)
    {
        layout.addStandaloneView(SearchView.ID, false, IPageLayout.TOP, 0.2f, layout
            .getEditorArea());
        layout.getViewLayout(SearchView.ID).setCloseable(false);
        layout.setEditorAreaVisible(true);

        // Display LogView for debugging.
        IFolderLayout horizontalFolder =
            layout.createFolder("bottomViews", IPageLayout.BOTTOM, .8f, layout
                .getEditorArea());
        horizontalFolder.addView("org.eclipse.pde.runtime.LogView");
        horizontalFolder.addView(DocumentListView.ID);

        IFolderLayout verticalFolder =
            layout.createFolder("leftViewa", IPageLayout.RIGHT, .7f, layout
                .getEditorArea());
        verticalFolder.addView(ClusterTreeView.ID);
    }
}
