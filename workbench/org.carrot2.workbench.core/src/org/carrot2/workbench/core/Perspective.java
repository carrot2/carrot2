package org.carrot2.workbench.core;

import org.carrot2.workbench.core.search.SearchView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory
{

    public void createInitialLayout(IPageLayout layout)
    {
        layout.addStandaloneView(SearchView.ID, false, IPageLayout.TOP, 0.5f, layout
            .getEditorArea());
        layout.setEditorAreaVisible(true);
    }
}
