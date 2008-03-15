/**
 * 
 */
package org.carrot2.workbench.core.ui.clusters;

import java.util.List;

import org.carrot2.core.ClusterWithParent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

final class ClusterTreeContentProvider implements ITreeContentProvider
{
    public Object [] getChildren(Object parentElement)
    {
        return ((ClusterWithParent) parentElement).subclusters
            .toArray(new ClusterWithParent [0]);
    }

    public Object getParent(Object element)
    {
        return ((ClusterWithParent) element).parent;
    }

    public boolean hasChildren(Object element)
    {
        List<ClusterWithParent> children = ((ClusterWithParent) element).subclusters;
        return (children != null && !children.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public Object [] getElements(Object inputElement)
    {
        return ((List<ClusterWithParent>) inputElement).toArray();
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}