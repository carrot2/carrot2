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

class ClusterSelectionProvider implements IPostSelectionProvider
{
    private final class SelectionListener implements ISelectionChangedListener
    {
        private List<ISelectionChangedListener> listeners;

        public SelectionListener(List<ISelectionChangedListener> listeners)
        {
            this.listeners = listeners;
        }

        public void selectionChanged(SelectionChangedEvent event)
        {
            SelectionChangedEvent event2 =
                new SelectionChangedEvent(ClusterSelectionProvider.this, getSelection());
            fireSelectionChanged(listeners, event2);
        }
    }

    private TreeViewer viewer;

    public ClusterSelectionProvider(TreeViewer viewer)
    {
        this.viewer = viewer;
        this.viewer
            .addSelectionChangedListener(new SelectionListener(selectionListeners));
        this.viewer.addPostSelectionChangedListener(new SelectionListener(
            postSelectionListeners));
    }

    List<ISelectionChangedListener> selectionListeners =
        new ArrayList<ISelectionChangedListener>();

    List<ISelectionChangedListener> postSelectionListeners =
        new ArrayList<ISelectionChangedListener>();

    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        selectionListeners.add(listener);
    }

    @SuppressWarnings("unchecked")
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
        selectionListeners.remove(listener);
    }

    public void setSelection(ISelection selection)
    {
        throw new NotImplementedException();
    }

    private void fireSelectionChanged(List<ISelectionChangedListener> listeners,
        SelectionChangedEvent event)
    {
        for (ISelectionChangedListener listener : listeners)
        {
            listener.selectionChanged(event);
        }
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener)
    {
        postSelectionListeners.add(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener)
    {
        postSelectionListeners.remove(listener);
    }

}