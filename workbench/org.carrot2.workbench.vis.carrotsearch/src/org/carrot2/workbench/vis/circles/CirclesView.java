
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2013, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.circles;

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.ui.IWorkbenchPart;

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
     * Create a document list for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;
        final CirclesViewPage page = new CirclesViewPage(editor);
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
