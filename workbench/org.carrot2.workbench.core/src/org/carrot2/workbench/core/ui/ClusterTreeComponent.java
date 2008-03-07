package org.carrot2.workbench.core.ui;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;

public class ClusterTreeComponent
{
    public TreeViewer createControls(Composite parent, Collection<Cluster> clusters)
    {
        TreeViewer tree = new TreeViewer(parent);
        tree.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                return ((ClusterWithParent) element).cluster.getLabel();
            }
        });
        tree.setContentProvider(new ITreeContentProvider()
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

        });
        List<ClusterWithParent> nodes = ClusterWithParent.wrap(new ArrayList<Cluster>(
            clusters));
        tree.setInput(nodes);
        return tree;
    }
}
