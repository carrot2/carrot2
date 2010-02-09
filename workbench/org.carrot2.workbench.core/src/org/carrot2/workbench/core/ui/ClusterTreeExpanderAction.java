
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.actions.ActionDelegate;

/**
 * Expand or collapses nodes of a {@link ClusterTree}.
 */
final class ClusterTreeExpanderAction extends ActionDelegate
{
    private ClusterTree tree;
    private boolean expand = true;
    private volatile IAction action;

    public ClusterTreeExpanderAction(ClusterTree tree, SearchResult searchResult)
    {
        this.tree = tree;

        searchResult.addListener(new ISearchResultListener() {
            public void processingResultUpdated(ProcessingResult result)
            {
                boolean hasStructure = false;
                for (Cluster c : result.getClusters())
                {
                    if (!c.getSubclusters().isEmpty())
                    {
                        hasStructure = true;
                        break;
                    }
                }

                if (action != null)
                {
                    action.setEnabled(hasStructure);
                }
            }
        });
    }

    public void init(IAction action)
    {
        this.action = action;
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
