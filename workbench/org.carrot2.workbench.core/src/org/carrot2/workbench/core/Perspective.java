package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.SearchView;
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
        IFolderLayout folder =
            layout.createFolder("bottomViews", IPageLayout.BOTTOM, .8f, layout
                .getEditorArea());
        folder.addView("org.eclipse.pde.runtime.LogView");
        folder.addView(DocumentListView.ID);
    }
}
