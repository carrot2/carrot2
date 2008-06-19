package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.views.*;
import org.eclipse.ui.*;

public class Perspective implements IPerspectiveFactory
{
    public static final String ID = "org.carrot2.workbench.core.perspective";

    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);

        IPlaceholderFolderLayout bottomFolder =
            layout.createPlaceholderFolder("bottomViews", IPageLayout.BOTTOM, .8f, layout
                .getEditorArea());
        bottomFolder.addPlaceholder("org.eclipse.pde.runtime.LogView");
        bottomFolder.addPlaceholder(DocumentListView.ID);

        IFolderLayout leftFolder =
            layout.createFolder("leftViews", IPageLayout.LEFT, .3f, layout
                .getEditorArea());
        leftFolder.addView(SearchView.ID);

        IPlaceholderFolderLayout leftBottomFolder =
            layout.createPlaceholderFolder("leftBottomViews", IPageLayout.BOTTOM, .3f,
                "leftViews");
        leftBottomFolder.addPlaceholder(ClusterTreeView.ID);
        leftBottomFolder.addPlaceholder(AttributesView.ID);

        addShortcuts(layout);
    }

    static void addShortcuts(IPageLayout layout)
    {
        layout.addShowViewShortcut(ClusterTreeView.ID);
        layout.addShowViewShortcut(DocumentListView.ID);
        layout.addShowViewShortcut(SearchView.ID);
        layout.addShowViewShortcut(AttributesView.ID);

        layout.addPerspectiveShortcut(TuningPerspective.ID);
    }
}
