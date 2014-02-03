
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.foamtree;

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.*;

/**
 * {@link FoamTreeView} displays clusters using browser-embedded Flash application.
 */
public final class FoamTreeView extends PageBookViewBase
{
    /**
     * Entry page for the view.
     */
    protected static final String ENTRY_PAGE = "/foamtree/index.vm";

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

    /**
     * Create a document list for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;
        final FoamTreeViewPage page = new FoamTreeViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof SearchEditor);
    }
}
