package org.carrot2.workbench.core.ui.views;

import org.carrot2.workbench.core.ui.DocumentListBrowser;
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
    }

    @Override
    public void dispose()
    {
        browserPart.dispose();
        super.dispose();
    }

}
