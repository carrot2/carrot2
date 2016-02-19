
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
            IPageLayout.LEFT, .35f, layout.getEditorArea());
        leftFolder.addView(SearchInputView.ID);        
    }
}
