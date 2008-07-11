package org.carrot2.workbench.core.ui;

import org.eclipse.ui.IWorkbenchPart;

/**
 * A view showing attribute values associated with the active editor's
 * {@link SearchResult}.
 */
public final class AttributeView extends PageBookViewBase
{
    public static final String ID = "org.carrot2.workbench.core.views.attributes";

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

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return part instanceof SearchEditor;
    }
}
