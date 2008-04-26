package org.carrot2.workbench.core.ui.views;

import org.carrot2.workbench.core.ui.ResultsEditor;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.*;

//FIXME: change superclass to PageBookView (this will save us all a lot of trouble)
public class ClusterTreeView extends PageBookView
{

    public static final String ID = "org.carrot2.workbench.core.clusters";

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
    protected PageRec doCreatePage(final IWorkbenchPart part)
    {
        Page partPage = new Page()
        {

            ClusterTreeComponent tree;

            @Override
            public void createControl(Composite parent)
            {
                tree = new ClusterTreeComponent();
                tree.init(getSite(), (ResultsEditor) part, parent);
            }

            @Override
            public Control getControl()
            {
                return tree.getControl();
            }

            @Override
            public void setFocus()
            {
                tree.getControl().setFocus();
            }

            @Override
            public void dispose()
            {
                tree.dispose();
            }
        };
        initPage(partPage);
        partPage.createControl(getPageBook());
        return new PageRec(part, partPage);
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
        IWorkbenchPart active = this.getSite().getPage().getActiveEditor();
        return (isImportant(active) ? active : null);
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof ResultsEditor);
    }
}
