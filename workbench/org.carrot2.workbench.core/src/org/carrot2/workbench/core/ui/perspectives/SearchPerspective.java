package org.carrot2.workbench.core.ui.perspectives;

import org.carrot2.workbench.core.ui.*;
import org.eclipse.ui.*;

/**
 * Search perspective provides plugins and layout suitable for beginner users.
 */
public final class SearchPerspective implements IPerspectiveFactory
{
    public static final String ID = "org.carrot2.workbench.core.perspective.search";

    /*
     * 
     */
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
        leftFolder.addView(SearchInputView.ID);

        IPlaceholderFolderLayout leftBottomFolder =
            layout.createPlaceholderFolder("leftBottomViews", IPageLayout.BOTTOM, .3f,
                "leftViews");
        leftBottomFolder.addPlaceholder(ClusterTreeView.ID);
        leftBottomFolder.addPlaceholder(AttributeView.ID);
    }
}
