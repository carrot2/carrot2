
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

package org.carrot2.workbench.vis.foamtree;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.vis.AbstractBrowserVisualizationViewPage;
import org.carrot2.workbench.vis.AbstractVisualizationView;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 * {@link FoamTreeView} displays clusters using browser-embedded Flash application.
 */
public final class FoamTreeView extends AbstractVisualizationView
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.views.foamtree";

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
    
        IToolBarManager toolbar = site.getActionBars().getToolBarManager();
        toolbar.add(new ToggleRelaxationAction());
        toolbar.add(new LayoutInitializerAction());
    }

    @Override
    protected AbstractBrowserVisualizationViewPage wrappedCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;
        final FoamTreeViewPage page = new FoamTreeViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());
        return page;
    }
}
