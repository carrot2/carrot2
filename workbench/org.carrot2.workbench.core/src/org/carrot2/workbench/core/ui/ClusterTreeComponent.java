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
    private final class ClusterTreeContentProvider implements ITreeContentProvider
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

    private final class ClusterLabelProvider extends LabelProvider
    {
        Image folderImage = CorePlugin.getImageDescriptor("icons/folder.gif")
            .createImage();

        @Override
        public String getText(Object element)
        {
            Cluster cluster = ((ClusterWithParent) element).cluster;
            return String.format("%s (%d)", cluster.getLabel(), cluster.getDocuments()
                .size());
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
    }

    public TreeViewer createControls(Composite parent, Collection<Cluster> clusters)
    {
        TreeViewer tree = new TreeViewer(parent);
        tree.setLabelProvider(new ClusterLabelProvider());
        tree.setContentProvider(new ClusterTreeContentProvider());
        List<ClusterWithParent> nodes = ClusterWithParent.wrap(new ArrayList<Cluster>(
            clusters));
        tree.setInput(nodes);
        return tree;
    }
}
