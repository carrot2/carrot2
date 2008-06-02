package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.views.*;
import org.eclipse.ui.*;

public class Perspective implements IPerspectiveFactory
{
    public static final String ID = "org.carrot2.workbench.core.perspective";

    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);

        IFolderLayout leftFolder =
            layout.createFolder("leftViews", IPageLayout.LEFT, .3f, layout
                .getEditorArea());
        leftFolder.addView(SearchView.ID);

        layout.addShowViewShortcut(ClusterTreeView.ID);
        layout.addShowViewShortcut(DocumentListView.ID);
        layout.addShowViewShortcut(SearchView.ID);
        layout.addShowViewShortcut(AttributesView.ID);

        layout.addPerspectiveShortcut(TuningPerspective.ID);
    }
}
