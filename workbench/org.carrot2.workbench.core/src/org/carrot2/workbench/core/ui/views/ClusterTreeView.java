package org.carrot2.workbench.core.ui.views;

import org.carrot2.workbench.core.ui.clusters.ClusterTreeComponent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ClusterTreeView extends ViewPart
{

    public static final String ID = "org.carrot2.workbench.core.clusters";

    ClusterTreeComponent tree;

    @Override
    public void createPartControl(Composite parent)
    {
        tree = new ClusterTreeComponent();
        tree.init(this.getSite(), parent);
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void dispose()
    {
        tree.dispose();
        super.dispose();
    }
}
