
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.circles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * {@link CirclesView} displays clusters using browser-embedded Flash application.
 */
public final class CirclesView extends PageBookViewBase
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.views.circles";

    /**
     * A map of unique identifiers and individual {@link CirclesViewPage}s
     * associated with opened editors.
     */
    final Map<Integer,CirclesViewPage> pages = Collections.synchronizedMap(
        new HashMap<Integer,CirclesViewPage>());

    /**
     * Sequencer used to generate unique page IDs.
     */
    private int sequencer;

    /**
     * Create a document list for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final int id = nextId();
        final CirclesViewPage page = new CirclesViewPage(editor, id);
        initPage(page);
        page.createControl(getPageBook());

        pages.put(id, page);

        return new PageRec(part, page);
    }
    
    /**
     * Generate a unique identifier for a single view page (associated
     * with a single editor). This identifier is used to identify HTTP
     * requests from external visualization code.
     */
    private synchronized int nextId()
    {
        return sequencer++;
    }

    /**
     * 
     */
    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        pages.remove(((CirclesViewPage) pageRecord.page).getId());
        super.doDestroyPage(part, pageRecord);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof SearchEditor);
    }

    /**
     * Get the active {@link CirclesView} or <code>null</code> if not
     * found. Must be called from UI thread. 
     */
    public final static CirclesView getActiveCirclesView()
    {
        final IWorkbenchWindow wb = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (wb != null)
        {
            final IWorkbenchPage wbPage = wb.getActivePage();
            if (wbPage != null)
            {
                for (IViewReference vr : wbPage.getViewReferences())
                {
                    final IViewPart view = vr.getView(false);
                    if (view != null && view instanceof CirclesView)
                    {
                        return (CirclesView) view;
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * Get the active {@link CirclesViewPage} associated with a
     * given editor or <code>null</code> if not found. Must be called from UI thread. 
     */
    public final static CirclesViewPage getActiveCirclesViewPage(int page)
    {
        final CirclesView view = getActiveCirclesView();
        if (view != null)
        {
            final CirclesViewPage viewPage = view.pages.get(page);
            if (viewPage != null)
            {
                return viewPage;
            }
        }
        
        return null;
    }
}
