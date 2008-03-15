/**
 * 
 */
package org.carrot2.workbench.core.ui.clusters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.eclipse.jface.viewers.*;

class ClusterSelectionProvider implements ISelectionProvider
{
    private TreeViewer viewer;

    public ClusterSelectionProvider(TreeViewer viewer)
    {
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent event)
            {
                SelectionChangedEvent event2 =
                    new SelectionChangedEvent(ClusterSelectionProvider.this,
                        getSelection());
                fireSelectionChanged(event2);
            }

        });
    }

    List<ISelectionChangedListener> listeners =
        new ArrayList<ISelectionChangedListener>();

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.add(listener);
    }

    public ISelection getSelection()
    {
        IStructuredSelection selectionWithParent =
            (IStructuredSelection) this.viewer.getSelection();
        List<ClusterWithParent> listWithParent = selectionWithParent.toList();
        List<Cluster> list = new ArrayList<Cluster>();
        for (ClusterWithParent clusterWithParent : listWithParent)
        {
            list.add(clusterWithParent.cluster);
        }
        return new StructuredSelection(list);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove(listener);
    }

    public void setSelection(ISelection selection)
    {
        throw new NotImplementedException();
    }

    private void fireSelectionChanged(SelectionChangedEvent event)
    {
        for (ISelectionChangedListener listener : listeners)
        {
            listener.selectionChanged(event);
        }
    }

}