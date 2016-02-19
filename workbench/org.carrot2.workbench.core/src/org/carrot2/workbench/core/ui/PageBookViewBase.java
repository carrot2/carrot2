
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

package org.carrot2.workbench.core.ui;

import org.eclipse.ui.*;
import org.eclipse.ui.part.*;

/**
 * Base implementation of {@link PageBookView} for views which have different pages for
 * every editor (similar to
 * <code>org.eclipse.ui.views.contentoutline.ContentOutline</code>).
 */
public abstract class PageBookViewBase extends PageBookView
{
    /**
     * Default page is blank.
     */
    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage defaultPage = new MessagePage();
        initPage(defaultPage);
        defaultPage.createControl(book);
        return defaultPage;
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        ((Page) pageRecord.page).dispose();
        pageRecord.dispose();
    }

    @Override
    protected IWorkbenchPart getBootstrapPart()
    {
        final IWorkbenchPage page = getSite().getPage();
        if (page != null)
        {
            return page.getActiveEditor();
        }

        return null;
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof IEditorPart);
    }
}
