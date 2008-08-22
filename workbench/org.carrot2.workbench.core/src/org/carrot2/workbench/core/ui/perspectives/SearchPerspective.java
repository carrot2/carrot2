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
        createCommonLayout(layout);
    }

    /**
     * Create common layout components and placeholders. Note
     * that views should be arranged via extension points (in plugin.xml),
     * relative to other existing views.
     */
    static void createCommonLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);

        final IFolderLayout leftFolder = layout.createFolder("leftViews",
            IPageLayout.LEFT, .3f, layout.getEditorArea());
        leftFolder.addView(SearchInputView.ID);        
    }
}
