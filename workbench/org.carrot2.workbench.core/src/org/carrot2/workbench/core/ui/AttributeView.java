
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

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.*;

/**
 * A view showing attribute values associated with the active editor's
 * {@link SearchResult}.
 */
public final class AttributeView extends PageBookViewBase
{
    public static final String ID = "org.carrot2.workbench.core.views.attributes";
    
    /**
     * Currently shown page.
     */
    private AttributeViewPage current;

    /**
     * Create a tree view for the given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final AttributeViewPage page = new AttributeViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }
    
    @Override
    protected void showPageRec(PageRec pageRec)
    {
        if (current != pageRec.page && (pageRec.page instanceof AttributeViewPage))
        {
            final AttributeViewPage next = (AttributeViewPage) pageRec.page;
            if (current != null) current.saveGlobalState();
            next.restoreGlobalState();
            current = next;
        }
        super.showPageRec(pageRec);
    }
    
    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage defaultPage = new MessagePage();
        defaultPage.setMessage("No active search result.");
        initPage(defaultPage);
        defaultPage.createControl(book);
        return defaultPage;
    }
    
    @Override
    public void partClosed(IWorkbenchPart part)
    {
        if (current != null) current.saveGlobalState();
        super.partClosed(part);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return part instanceof SearchEditor;
    }
}
