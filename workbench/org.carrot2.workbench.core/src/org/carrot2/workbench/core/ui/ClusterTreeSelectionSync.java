package org.carrot2.workbench.core.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.*;

/**
 * Updates {@link ClusterTree}'s selection with the current selection state originating
 * from a given {@link SearchEditor}.
 */
final class ClusterTreeSelectionSync implements ISelectionChangedListener
{
    private final ClusterTree clusterTree;

    /**
     * Cache last selection that affected the documents view and do not repeat it, until
     * it changes.
     */
    private ISelection lastSelection;

    /*
     * 
     */
    public ClusterTreeSelectionSync(ClusterTree tree)
    {
        this.clusterTree = tree;
    }

    /*
     * 
     */
    public void selectionChanged(SelectionChangedEvent event)
    {
        final Object part = event.getSource();
        final ISelection selection = event.getSelection();

        Logger.getLogger(ClusterTreeSelectionSync.class).debug(
            "Received selection [part=" + part + ", receipient: " + this + "]");

        // Skip redundant selection events that occur when switching between
        // parts (switching editors, for example).
        if (lastSelection == selection
            || (lastSelection != null && selection.equals(lastSelection)))
        {
            return;
        }

        if (selection instanceof IStructuredSelection)
        {
            /*
             * Update the editor's selection in the view's tree.
             */
            clusterTree.setSelection(selection);

            lastSelection = selection;
        }
    }
}
