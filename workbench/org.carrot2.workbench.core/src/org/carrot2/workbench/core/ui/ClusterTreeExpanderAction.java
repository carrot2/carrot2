
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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
    public enum CollapseAction {
        COLLAPSE("icons/collapseall.png", "Collapse all clusters."),
        EXPAND("icons/expandall.png", "Expand all clusters.");
        
        final String iconPath;
        final String hint;
        
        private CollapseAction(String iconPath, String hint)
        {
            this.iconPath = iconPath;
            this.hint = hint;
        }
    }

    private final CollapseAction collapseAction;
    private ClusterTree tree;
    private volatile IAction action;

    public ClusterTreeExpanderAction(CollapseAction collapseAction, ClusterTree tree, SearchResult searchResult)
    {
        this.tree = tree;
        this.collapseAction = collapseAction;

        searchResult.addListener(new SearchResultListenerAdapter() {
            public void processingResultUpdated(ProcessingResult result)
            {
                boolean hasStructure = false;
                for (Cluster c : Cluster.flatten(result.getClusters()))
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
        action.setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor(collapseAction.iconPath));
        action.setToolTipText(collapseAction.hint);
    }

    public void run(IAction action)
    {
        if (collapseAction == CollapseAction.EXPAND)
            tree.expandAll();
        else
            tree.collapseAll();
    }
}
