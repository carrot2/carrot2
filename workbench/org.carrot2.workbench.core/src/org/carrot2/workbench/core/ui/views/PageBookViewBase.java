package org.carrot2.workbench.core.ui.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.*;

public abstract class PageBookViewBase extends PageBookView
{

    @Override
    protected IPage createDefaultPage(PageBook book)
    {
        MessagePage defaultPage = new MessagePage();
        initPage(defaultPage);
        defaultPage.createControl(book);
        defaultPage.setMessage("Nothing to show for this editor");
        return defaultPage;
    }

    @Override
    protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        ((Page) pageRecord.page).dispose();
        pageRecord.dispose();
    }

    @Override
    protected IWorkbenchPart getBootstrapPart() {
        final IWorkbenchPage page = getSite().getPage();
        if (page != null) {
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