package org.carrot2.workbench.core.ui.views;

import org.carrot2.core.Cluster;
import org.carrot2.workbench.core.ui.DocumentListBrowser;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DocumentListView extends ViewPart
{
    public static final String ID = "org.carrot2.workbench.core.documents";

    private DocumentListBrowser browserPart;
    private Image titleImage;

    @Override
    public void createPartControl(Composite parent)
    {
        browserPart = new DocumentListBrowser();
        browserPart.init(this.getSite(), parent);
        browserPart.populateToolbar(getViewSite().getActionBars().getToolBarManager());
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
    public Image getTitleImage()
    {
        titleImage =
            AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui",
                "icons/full/obj16/file_obj.gif").createImage();
        return titleImage;
    }

    @Override
    public void dispose()
    {
        if (titleImage != null && !titleImage.isDisposed())
        {
            titleImage.dispose();
        }
        browserPart.dispose();
        super.dispose();
    }

}
