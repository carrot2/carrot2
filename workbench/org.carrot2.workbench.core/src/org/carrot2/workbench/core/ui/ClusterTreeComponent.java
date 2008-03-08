package org.carrot2.workbench.core.ui;

import java.util.*;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class ClusterTreeComponent
{
    public TreeViewer createControls(Composite parent, Collection<Cluster> clusters)
    {
        TreeViewer tree = new TreeViewer(parent);
        tree.setLabelProvider(new LabelProvider()
        {
            Image folderImage = CorePlugin.getImageDescriptor("icons/folder.gif")
                .createImage();

            @Override
            public String getText(Object element)
            {
                return ((ClusterWithParent) element).cluster.getLabel();
            }

            @Override
            public Image getImage(Object element)
            {
                return folderImage;
            }

            @Override
            public void dispose()
            {
                folderImage.dispose();
                super.dispose();
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

        });
        List<ClusterWithParent> nodes = ClusterWithParent.wrap(new ArrayList<Cluster>(
            clusters));
        tree.setInput(nodes);
        return tree;
    }
}
