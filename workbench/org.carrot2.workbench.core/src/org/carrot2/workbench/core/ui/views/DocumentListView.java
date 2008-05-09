package org.carrot2.workbench.core.ui.views;

import org.carrot2.core.Cluster;
import org.carrot2.workbench.core.ui.DocumentListBrowser;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class DocumentListView extends ViewPart
{
    public static final String ID = "org.carrot2.workbench.core.documents";

    private DocumentListBrowser browserPart;

    @Override
    public void createPartControl(Composite parent)
    {
        browserPart = new DocumentListBrowser();
        browserPart.init(this.getSite(), parent);
    }

    @Override
    public void setFocus()
    {
        ISelection selection = getSite().getPage().getSelection();
        if (selection != null && !selection.isEmpty()
            && selection instanceof IStructuredSelection)
        {
            IStructuredSelection selected = (IStructuredSelection) selection;
            if (selected.size() == 1 && selected.getFirstElement() instanceof Cluster)
            {
                browserPart.updateBrowserText((Cluster) selected.getFirstElement());
            }
        }
    }

    @Override
    public void dispose()
    {
        browserPart.dispose();
        super.dispose();
    }

}
