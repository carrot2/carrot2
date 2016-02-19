
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

package org.carrot2.workbench.vis.circles;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.vis.AbstractBrowserVisualizationViewPage;
import org.carrot2.workbench.vis.AbstractVisualizationView;
import org.eclipse.ui.IWorkbenchPart;

/**
 * {@link CirclesView} displays clusters using browser-embedded Flash application.
 */
public final class CirclesView extends AbstractVisualizationView
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.views.circles";


    @Override
    protected AbstractBrowserVisualizationViewPage wrappedCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;
        final CirclesViewPage page = new CirclesViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());
        return page;
    }
}
