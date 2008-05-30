package org.carrot2.workbench.core.ui.views;

import org.carrot2.workbench.core.ui.ResultsEditor;
import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ClusterTreeView extends PageBookView
{

    public static final String ID = "org.carrot2.workbench.core.clusters";

    private Image titleImage;

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
        if (!(part instanceof ResultsEditor))
        {
            return null;
        }
        Page partPage = new Page()
        {

            @Override
            public void init(IPageSite pageSite)
            {
                super.init(pageSite);
                tree = new ClusterTreeComponent();
                tree.populateToolbar(pageSite.getActionBars().getToolBarManager());
            }

            ClusterTreeComponent tree;

            @Override
            public void createControl(Composite parent)
            {
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
        return this.getSite().getPage().getActiveEditor();
    }

    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof IEditorPart);
    }

    @Override
    public Image getTitleImage()
    {
        titleImage =
            AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui",
                "icons/full/eview16/filenav_nav.gif").createImage();
        return titleImage;
    }

    @Override
    public void dispose()
    {
        if (titleImage != null && !titleImage.isDisposed())
        {
            titleImage.dispose();
        }
        super.dispose();
    }
}
