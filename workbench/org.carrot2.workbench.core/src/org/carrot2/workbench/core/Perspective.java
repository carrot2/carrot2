package org.carrot2.workbench.core;

import org.carrot2.workbench.core.ui.SearchView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory
{
    public void createInitialLayout(IPageLayout layout)
    {
        // TODO: This code does not seem to be invoked (ever). What gives?!
        layout.addStandaloneView(SearchView.ID, false, IPageLayout.TOP, 0.2f, layout
            .getEditorArea());
        layout.getViewLayout(SearchView.ID).setCloseable(false);
        layout.setEditorAreaVisible(true);

        // Display LogView for debugging.
        layout.addStandaloneView("org.eclipse.pde.runtime.LogView", true,
            IPageLayout.BOTTOM, .2f, layout.getEditorArea());
    }
}
