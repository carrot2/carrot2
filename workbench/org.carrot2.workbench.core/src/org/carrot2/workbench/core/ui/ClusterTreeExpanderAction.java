package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Expand or collapses nodes of a {@link ClusterTree}.
 */
public final class ClusterTreeExpanderAction extends ActionDelegate
{
    private ClusterTree tree;
    private boolean expand = true;

    public ClusterTreeExpanderAction(ClusterTree tree)
    {
        this.tree = tree;
    }

    public void init(IAction action)
    {
        update(action);
    }

    private void update(IAction action)
    {
        action
            .setImageDescriptor(WorkbenchCorePlugin
                .getImageDescriptor(
                    expand 
                    ? "icons/expandall.png"
                    : "icons/collapseall.png"));
        action.setToolTipText("Toggle expand/collapse clusters");
    }

    public void run(IAction action)
    {
        if (expand) tree.expandAll();
        else tree.collapseAll();

        expand = !expand;
        update(action);
    }
}
